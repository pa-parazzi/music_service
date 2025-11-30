package org.musicservice.demo.service.music;

import org.musicservice.demo.dto.music.SoundDto;
import org.musicservice.demo.dto.music.mainResponse.AlbumResponse;
import org.musicservice.demo.dto.music.mainResponse.ArtistResponse;
import org.musicservice.demo.exception.music.ArtistNotFoundException;
import org.musicservice.demo.mapper.music.ArtistMapper;
import org.musicservice.demo.model.music.Artist;
import org.musicservice.demo.model.music.Sound;
import org.musicservice.demo.repository.music.ArtistRepository;
import org.musicservice.demo.repository.music.SoundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final ArtistMapper artistMapper;
    private final SoundService soundService;

    @Autowired
    public ArtistService(ArtistRepository artistRepository, ArtistMapper artistMapper, SoundService soundService) {
        this.artistRepository = artistRepository;
        this.artistMapper = artistMapper;
        this.soundService = soundService;
    }

    public Artist findById(Long artistId){
        return artistRepository.findById(artistId).orElseThrow(() -> new ArtistNotFoundException("Исполнителя с таким id не существует"));
    }

    public ArtistResponse viewArtist(Long artistId){
        Artist findArtist = findById(artistId);
        ArtistResponse artistResponse = artistMapper.toArtistResponse(findArtist);
        List<SoundDto> soundDtoList = soundService.getSoundListByArtistId(artistId);
        artistResponse.setSoundList(soundDtoList);
        return artistResponse;
    }
}
