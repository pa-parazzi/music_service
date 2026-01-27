package org.musicservice.demo.service.search;

import org.musicservice.demo.dto.music.artist.ArtistResponse;
import org.musicservice.demo.repository.music.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SearchArtistService {

    private final ArtistRepository artistRepository;

    @Autowired
    public SearchArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    public List<ArtistResponse> findAllArtistStartingWith(String fragment){
        if(fragment == null || fragment.trim().isBlank()) return null;
        return artistRepository.findAllByNameStartingWith(fragment);
    }
}
