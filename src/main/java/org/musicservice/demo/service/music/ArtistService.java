package org.musicservice.demo.service.music;

import org.musicservice.demo.dto.music.artist.ArtistResponse;
import org.musicservice.demo.exception.music.MusicNotFoundException;
import org.musicservice.demo.repository.music.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ArtistService {

    private final ArtistRepository artistRepository;

    @Autowired
    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    public ArtistResponse viewArtistById(Long artistId){
        return artistRepository.findArtistResponseById(artistId).orElseThrow(() -> new MusicNotFoundException("Artist with id: " + artistId + " not found"));
    }
}
