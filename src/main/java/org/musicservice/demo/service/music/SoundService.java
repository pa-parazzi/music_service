package org.musicservice.demo.service.music;

import org.musicservice.demo.exception.music.SoundNotFoundException;
import org.musicservice.demo.model.music.Sound;
import org.musicservice.demo.repository.music.SoundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SoundService {

    private final SoundRepository soundRepository;

    @Autowired
    public SoundService(SoundRepository soundRepository) {
        this.soundRepository = soundRepository;
    }

    public Sound findById(Long id){
        return soundRepository.findById(id).orElseThrow(()-> new SoundNotFoundException("песня не найдена"));
    }
}
