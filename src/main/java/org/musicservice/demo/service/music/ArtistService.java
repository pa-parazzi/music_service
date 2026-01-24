package org.musicservice.demo.service.music;

import org.musicservice.demo.dto.music.artist.ArtistResponse;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.exception.ApiNotFoundException;
import org.musicservice.demo.mapper.music.ArtistMapper;
import org.musicservice.demo.repository.music.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final ArtistMapper artistMapper;

    @Autowired
    public ArtistService(ArtistRepository artistRepository, ArtistMapper artistMapper) {
        this.artistRepository = artistRepository;
        this.artistMapper = artistMapper;
    }

    public Artist findById(Long artistId){
        return artistRepository.findById(artistId).orElseThrow(() -> new ApiNotFoundException("Artist with id: " + artistId + " not found"));
    }

    public ArtistResponse viewArtistById(Long artistId){
        return artistMapper.toResponse(findById(artistId));
    }
}
