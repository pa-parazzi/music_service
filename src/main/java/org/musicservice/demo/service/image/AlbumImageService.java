package org.musicservice.demo.service.image;

import org.musicservice.demo.configuration.YandexCloud.YandexStorageProperties;
import org.musicservice.demo.dto.image.AlbumImageDto;
import org.musicservice.demo.mapper.image.AlbumImageMapper;
import org.musicservice.demo.model.image.AlbumImage;
import org.musicservice.demo.repository.image.AlbumImageRepository;
import org.musicservice.demo.service.s3.S3UrlGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AlbumImageService {

    private final AlbumImageMapper albumImageMapper;
    private final AlbumImageRepository albumImageRepository;
    private final YandexStorageProperties yandexStorageProperties;
    private final S3UrlGenerator s3UrlGenerator;

    @Autowired
    public AlbumImageService(AlbumImageMapper albumImageMapper, AlbumImageRepository albumImageRepository, YandexStorageProperties yandexStorageProperties, S3UrlGenerator s3UrlGenerator) {
        this.albumImageMapper = albumImageMapper;
        this.albumImageRepository = albumImageRepository;
        this.yandexStorageProperties = yandexStorageProperties;
        this.s3UrlGenerator = s3UrlGenerator;
    }

    public AlbumImageDto getAlbumImageByAlbumId(Long albumId){
        AlbumImage albumImage = albumImageRepository.findByAlbumId(albumId);
        AlbumImageDto albumImageDto = albumImageMapper.convertToDto(albumImage);
        String imgKey = albumImage.getS3Key();
        String url = s3UrlGenerator.generatePublicUrl(yandexStorageProperties.getBuckets().get("img"), imgKey);
        albumImageDto.setKey(imgKey);
        albumImageDto.setUrl(url);
        return albumImageDto;
    }
}
