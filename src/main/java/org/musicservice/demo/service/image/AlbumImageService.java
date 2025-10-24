package org.musicservice.demo.service.image;

import org.musicservice.demo.configuration.YandexCloud.YandexStorageProperties;
import org.musicservice.demo.dto.image.AlbumImageDto;
import org.musicservice.demo.mapper.image.AlbumImageMapper;
import org.musicservice.demo.model.image.AlbumImage;
import org.musicservice.demo.model.music.Album;
import org.musicservice.demo.repository.image.AlbumImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class AlbumImageService {

    private final AlbumImageRepository albumImageRepository;
    private final YandexStorageProperties yandexStorageProperties;
    private final S3ImgUrlGenerator s3ImgUrlGenerator;
    private final S3Client s3Client;
    private final AlbumImageMapper albumImageMapper;

    @Autowired
    public AlbumImageService(AlbumImageRepository albumImageRepository, YandexStorageProperties yandexStorageProperties, S3ImgUrlGenerator s3ImgUrlGenerator, S3Client s3Client, AlbumImageMapper albumImageMapper) {
        this.albumImageRepository = albumImageRepository;
        this.yandexStorageProperties = yandexStorageProperties;
        this.s3ImgUrlGenerator = s3ImgUrlGenerator;
        this.s3Client = s3Client;
        this.albumImageMapper = albumImageMapper;
    }

    @Transactional
    public void create(MultipartFile file, Album album){
        String key = "album_image/" + UUID.randomUUID() + file.getOriginalFilename();
        try(InputStream inputStream = file.getInputStream()){
            s3Client.putObject(PutObjectRequest.builder()
                    .bucket(yandexStorageProperties.getBuckets().get("img"))
                    .key(key)
                    .contentType(file.getContentType())
                    .build(), RequestBody.fromInputStream(inputStream, file.getSize()));
        } catch (IOException e){
            throw new RuntimeException("Ошибка загрузки изображения для альбома в s3: " + e);
        }
        String url = s3ImgUrlGenerator.generatePresignedUploadUrlImg(yandexStorageProperties.getBuckets().get("img"), key, file);
        AlbumImage albumImage = setParams(album, key, url);
        albumImageRepository.save(albumImage);
    }

    private AlbumImage setParams(Album album, String key, String url){
        AlbumImage albumImage = new AlbumImage();
        albumImage.setAlbum(album);
        albumImage.setS3Key(key);
        album.setImage(albumImage);
        AlbumImageDto dto = albumImageMapper.convertToDto(albumImage);
        dto.setKey(key);
        dto.setUrl(url);
        return albumImage;
    }
}
