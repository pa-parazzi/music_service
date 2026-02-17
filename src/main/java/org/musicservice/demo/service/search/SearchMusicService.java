package org.musicservice.demo.service.search;

import org.musicservice.demo.dto.music.artist.ArtistResponse;
import org.musicservice.demo.dto.music.album.AlbumResponse;
import org.musicservice.demo.dto.music.search.SearchMusicResponse;
import org.musicservice.demo.exception.NoSuchMusicResultException;
import org.musicservice.demo.mapper.music.AlbumMapper;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.musicservice.demo.repository.music.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SearchMusicService {

    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;

    @Autowired
    public SearchMusicService(ArtistRepository artistRepository, AlbumRepository albumRepository, AlbumMapper albumMapper) {
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
        this.albumMapper = albumMapper;
    }

    public SearchMusicResponse searchMusicResult(String fragment){
        if(isEmpty(fragment)) throw new NoSuchMusicResultException("Ничего не найдено");
        List<ArtistResponse> artists = artistRepository.findAllByNameStartingWith(fragment);
        List<AlbumResponse> albumResponses = albumRepository.findAllByTitleStartingWith(fragment).stream().map(albumMapper::toAlbumResponse).toList();
        return new SearchMusicResponse(artists, albumResponses);
    }

    private boolean isEmpty(String fragment){
        return fragment == null || fragment.trim().isEmpty();
    }
}
