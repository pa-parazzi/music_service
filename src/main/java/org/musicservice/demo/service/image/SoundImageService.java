package org.musicservice.demo.service.image;

import org.musicservice.demo.configuration.YandexCloud.YandexStorageProperties;
import org.musicservice.demo.dto.image.SoundImageDto;
import org.musicservice.demo.mapper.image.SoundImageMapper;
import org.musicservice.demo.model.image.AlbumImage;
import org.musicservice.demo.model.image.SoundImage;
import org.musicservice.demo.model.music.Sound;
import org.musicservice.demo.repository.image.SoundImageRepository;
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
public class SoundImageService {

    private final SoundImageRepository soundImageRepository;
    private final YandexStorageProperties yandexStorageProperties;
    private final S3ImgUrlGenerator s3ImgUrlGenerator;
    private final S3Client s3Client;
    private final SoundImageMapper soundImageMapper;

    @Autowired
    public SoundImageService(SoundImageRepository soundImageRepository, YandexStorageProperties yandexStorageProperties, S3ImgUrlGenerator s3ImgUrlGenerator, S3Client s3Client, SoundImageMapper soundImageMapper) {
        this.soundImageRepository = soundImageRepository;
        this.yandexStorageProperties = yandexStorageProperties;
        this.s3ImgUrlGenerator = s3ImgUrlGenerator;
        this.s3Client = s3Client;
        this.soundImageMapper = soundImageMapper;
    }

    @Transactional
    public void create(MultipartFile file, Sound sound){
        String key = "sound_image/" + UUID.randomUUID() + file.getOriginalFilename();
        try(InputStream inputStream = file.getInputStream()){
            s3Client.putObject(PutObjectRequest.builder()
                    .bucket(yandexStorageProperties.getBuckets().get("img"))
                    .key(key)
                    .contentType(file.getContentType())
                    .build(), RequestBody.fromInputStream(inputStream, file.getSize()));
        } catch (IOException e){
            throw new RuntimeException("Ошибка загрузки изображения для трека в s3: " + e);
        }
        String url = s3ImgUrlGenerator.generatePresignedUploadUrlImg(yandexStorageProperties.getBuckets().get("img"), key, file);
        SoundImage soundImage = setParams(sound, key, url);
        soundImageRepository.save(soundImage);
    }

    private SoundImage setParams(Sound sound, String key, String url){
        SoundImage soundImage = new SoundImage();
        soundImage.setSound(sound);
        soundImage.setS3Key(key);
        sound.setImage(soundImage);
        SoundImageDto dto = soundImageMapper.convertToDto(soundImage);
        dto.setKey(key);
        dto.setUrl(url);
        return soundImage;
    }
}
