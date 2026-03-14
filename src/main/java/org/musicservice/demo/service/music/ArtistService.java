package org.musicservice.demo.service.music;

import org.musicservice.demo.dto.music.artist.ArtistResponse;
import org.musicservice.demo.dto.music.artist.ArtistsResponse;
import org.musicservice.demo.exception.music.MusicNotFoundException;
import org.musicservice.demo.repository.music.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final GenreService genreService;

    @Autowired
    public ArtistService(ArtistRepository artistRepository, GenreService genreService) {
        this.artistRepository = artistRepository;
        this.genreService = genreService;
    }

    public ArtistResponse viewArtistById(Long artistId){
        return artistRepository.findArtistResponseById(artistId).orElseThrow(() -> new MusicNotFoundException("Artist with id: " + artistId + " not found"));
    }

    public ArtistsResponse findAllArtistsByGenreId(Long genreId){
        genreService.checkExistById(genreId);
        List<ArtistResponse> artistResponseList = artistRepository.findAllArtistResponseByGenreId(genreId);
        return new ArtistsResponse(artistResponseList);
    }
}
