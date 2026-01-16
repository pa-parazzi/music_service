package org.musicservice.demo.service.music;

import org.musicservice.demo.dto.like.LikedSoundResponse;
import org.musicservice.demo.dto.music.sound.SoundDto;
import org.musicservice.demo.dto.music.sound.CollectionTracksResponse;
import org.musicservice.demo.exception.music.SoundNotFoundException;
import org.musicservice.demo.mapper.music.SoundMapper;
import org.musicservice.demo.entity.music.Sound;
import org.musicservice.demo.repository.music.SoundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SoundService {

    private final SoundRepository soundRepository;
    private final SoundMapper soundMapper;

    @Autowired
    public SoundService(SoundRepository soundRepository, SoundMapper soundMapper) {
        this.soundRepository = soundRepository;
        this.soundMapper = soundMapper;
    }

    public Sound findById(Long id){
        return soundRepository.findById(id).orElseThrow(()-> new SoundNotFoundException("песня не найдена"));
    }

    public List<SoundDto> getSoundListByAlbumId(Long albumId){
        return soundRepository.findAllByAlbumId(albumId).stream().map(soundMapper::toDto).toList();
    }

    public CollectionTracksResponse getTrackCollectionByUserLikes(List<LikedSoundResponse> responses){
        List<Long> soundIds = responses.stream().map(LikedSoundResponse::getSoundId).toList();
        List<SoundDto> soundDtoList = soundRepository.findAllByIdForCollectionPage(soundIds).stream().map(soundMapper::toDto).toList();
        CollectionTracksResponse collectionTracks = new CollectionTracksResponse();
        collectionTracks.setSoundList(soundDtoList);
        return collectionTracks;
    }
}
