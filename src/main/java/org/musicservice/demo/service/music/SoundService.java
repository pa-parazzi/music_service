package org.musicservice.demo.service.music;

import org.musicservice.demo.configuration.YandexCloud.YandexStorageProperties;
import org.musicservice.demo.dto.image.SoundImageDto;
import org.musicservice.demo.dto.music.SoundDto;
import org.musicservice.demo.mapper.image.SoundImageMapper;
import org.musicservice.demo.mapper.music.SoundMapper;
import org.musicservice.demo.model.image.SoundImage;
import org.musicservice.demo.repository.music.SoundRepository;
import org.musicservice.demo.service.s3.S3ImgUrlGenerator;
import org.musicservice.demo.service.s3.S3TrackUrlGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SoundService {


    private final YandexStorageProperties yandexStorageProperties;
    private final S3ImgUrlGenerator s3ImgUrlGenerator;
    private final S3TrackUrlGenerator s3TrackUrlGenerator;
    private final SoundRepository soundRepository;
    private final SoundMapper soundMapper;
    private final SoundImageMapper soundImageMapper;

    @Autowired
    public SoundService(YandexStorageProperties yandexStorageProperties, S3ImgUrlGenerator s3ImgUrlGenerator, S3TrackUrlGenerator s3TrackUrlGenerator, SoundRepository soundRepository, SoundMapper soundMapper, SoundImageMapper soundImageMapper) {
        this.yandexStorageProperties = yandexStorageProperties;
        this.s3ImgUrlGenerator = s3ImgUrlGenerator;
        this.s3TrackUrlGenerator = s3TrackUrlGenerator;
        this.soundRepository = soundRepository;
        this.soundMapper = soundMapper;
        this.soundImageMapper = soundImageMapper;
    }

    public List<SoundDto> soundList(){
        return soundRepository.findAll().stream().map(sound -> {
            SoundImage soundImage = sound.getImage();
            SoundImageDto soundImageDto = soundImageMapper.convertToDto(soundImage);
            String trackUrl = s3TrackUrlGenerator.generatePresignedUrl(yandexStorageProperties.getBuckets().get("music"), sound.getKey());
            String imgUrl = s3ImgUrlGenerator.generatePublicUrl(yandexStorageProperties.getBuckets().get("img"), soundImage.getS3Key());
            SoundDto soundDto = soundMapper.convertToDto(sound);
            soundImageDto.setKey(soundImage.getS3Key());
            soundImageDto.setUrl(imgUrl);
            soundDto.setUrl(trackUrl);
            soundDto.setSoundImage(soundImageDto);
            return soundDto;
        }).toList();
    }
}
