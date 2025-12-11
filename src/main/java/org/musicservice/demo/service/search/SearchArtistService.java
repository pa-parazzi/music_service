package org.musicservice.demo.service.search;

import org.musicservice.demo.dto.music.ArtistDto;
import org.musicservice.demo.dto.music.response.ArtistResponse;
import org.musicservice.demo.mapper.music.ArtistMapper;
import org.musicservice.demo.model.music.Artist;
import org.musicservice.demo.repository.music.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SearchArtistService {

    private final ArtistRepository artistRepository;
    private final ArtistMapper artistMapper;

    @Autowired
    public SearchArtistService(ArtistRepository artistRepository, ArtistMapper artistMapper) {
        this.artistRepository = artistRepository;
        this.artistMapper = artistMapper;
    }

    public List<ArtistDto> findAllArtistStartingWith(String fragment){
        if(fragment == null || fragment.trim().isBlank()) return null;
        return artistRepository.findAllByNameStartingWith(fragment).stream().map(artistMapper::toDto).toList();
    }
}
