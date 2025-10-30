package org.musicservice.demo.service.music;

import org.musicservice.demo.configuration.YandexCloud.YandexStorageProperties;
import org.musicservice.demo.dto.music.SoundDto;
import org.musicservice.demo.mapper.music.SoundMapper;
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
    private final S3TrackUrlGenerator s3TrackUrlGenerator;
    private final SoundRepository soundRepository;
    private final SoundMapper soundMapper;

    @Autowired
    public SoundService(YandexStorageProperties yandexStorageProperties, S3TrackUrlGenerator s3TrackUrlGenerator, SoundRepository soundRepository, SoundMapper soundMapper) {
        this.yandexStorageProperties = yandexStorageProperties;
        this.s3TrackUrlGenerator = s3TrackUrlGenerator;
        this.soundRepository = soundRepository;
        this.soundMapper = soundMapper;
    }

    public List<SoundDto> soundList(){
        return soundRepository.findAll().stream().map(sound -> {
            String trackUrl = s3TrackUrlGenerator.generatePublicUrl(yandexStorageProperties.getBuckets().get("music"), sound.getKey());
            SoundDto soundDto = soundMapper.convertToDto(sound);
            soundDto.setUrl(trackUrl);
            return soundDto;
        }).toList();
    }
}
