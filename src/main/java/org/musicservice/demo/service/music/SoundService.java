package org.musicservice.demo.service.music;

import org.musicservice.demo.dto.music.SoundDto;
import org.musicservice.demo.dto.music.response.CollectionTracksResponse;
import org.musicservice.demo.dto.music.response.LikeResponse;
import org.musicservice.demo.exception.music.SoundNotFoundException;
import org.musicservice.demo.mapper.music.SoundMapper;
import org.musicservice.demo.model.music.Sound;
import org.musicservice.demo.repository.music.SoundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

    public CollectionTracksResponse getTrackCollectionByUserLikes(List<LikeResponse> responses){
        List<Long> soundIds = responses.stream().map(LikeResponse::getTargetId).toList();
        Map<Long, Integer> orderSoundIds = new HashMap<>();
        for (int i = 0; i < soundIds.size(); i++) {
            orderSoundIds.put(soundIds.get(i), i);
        }

        List<SoundDto> soundDtoList =  soundRepository.findAllById(soundIds).stream()
                .map(soundMapper::toDto)
                .sorted(Comparator.comparingInt(dto -> orderSoundIds.get(dto.getId()))).toList();

        CollectionTracksResponse collectionTracksResponse = new CollectionTracksResponse();
        collectionTracksResponse.setSoundList(soundDtoList);
        return collectionTracksResponse;
    }
}
