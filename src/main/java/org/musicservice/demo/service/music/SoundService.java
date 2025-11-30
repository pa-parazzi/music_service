package org.musicservice.demo.service.music;

import org.musicservice.demo.configuration.YandexCloud.YandexStorageProperties;
import org.musicservice.demo.dto.music.SoundDto;
import org.musicservice.demo.mapper.music.SoundMapper;
import org.musicservice.demo.model.music.Sound;
import org.musicservice.demo.repository.music.SoundRepository;
import org.musicservice.demo.service.s3.S3UrlGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SoundService {

    private final SoundRepository soundRepository;
    private final SoundMapper soundMapper;
    private final YandexStorageProperties yandexStorageProperties;
    private final S3UrlGenerator s3UrlGenerator;

    @Autowired
    public SoundService(SoundRepository soundRepository, SoundMapper soundMapper, YandexStorageProperties yandexStorageProperties, S3UrlGenerator s3UrlGenerator) {
        this.soundRepository = soundRepository;
        this.soundMapper = soundMapper;
        this.yandexStorageProperties = yandexStorageProperties;
        this.s3UrlGenerator = s3UrlGenerator;
    }

    public List<SoundDto> getSoundListByAlbumId(Long albumId){
        List<Sound> soundList = soundRepository.findByAlbumId(albumId);
        return getSoundListDto(soundList);
    }

    public List<SoundDto> getSoundListByArtistId(Long artistId){
        List<Sound> soundList = soundRepository.findByArtistId(artistId);
        return getSoundListDto(soundList);
    }

    private List<SoundDto> getSoundListDto(List<Sound> soundList){
        return soundList.stream().map(sound -> {
            String soundKey = sound.getKey();
            String soundUrl = s3UrlGenerator.generatePublicUrl(yandexStorageProperties.getBuckets().get("music"), soundKey);
            SoundDto soundDto = soundMapper.toDto(sound);
            soundDto.setKey(soundKey);
            soundDto.setUrl(soundUrl);
            return soundDto;
        }).toList();
    }
}
