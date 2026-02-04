package org.musicservice.demo.service.music;

import org.musicservice.demo.dto.like.LikedSoundResponse;
import org.musicservice.demo.dto.music.sound.CollectionTracksResponse;
import org.musicservice.demo.dto.music.sound.SoundResponse;
import org.musicservice.demo.mapper.music.SoundMapper;
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

    public List<SoundResponse> getSoundListByArtistId(Long artistId){
        return soundRepository.findAllByArtistId(artistId).stream().map(soundMapper::toResponse).toList();
    }

    public List<SoundResponse> getSoundListByAlbumId(Long albumId){
        return soundRepository.findAllByAlbumId(albumId).stream().map(soundMapper::toResponse).toList();
    }

    public CollectionTracksResponse getTrackCollectionByUserLikes(List<LikedSoundResponse> responses){
        List<Long> soundOrderIds = responses.stream().map(LikedSoundResponse::getSoundId).toList();
        Long[] orderIds = soundOrderIds.toArray(Long[]::new);
        List<SoundResponse> response = soundRepository.findAllByIdForCollectionPage(orderIds).stream().map(soundMapper::toResponse).toList();
        return new CollectionTracksResponse(response);
    }
}
