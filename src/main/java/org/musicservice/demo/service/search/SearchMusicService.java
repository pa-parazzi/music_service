package org.musicservice.demo.service.search;

import org.musicservice.demo.dto.music.artist.ArtistResponse;
import org.musicservice.demo.dto.music.album.AlbumResponse;
import org.musicservice.demo.dto.music.search.SearchMusicResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SearchMusicService {

    private final SearchArtistService searchArtistService;
    private final SearchAlbumService searchAlbumService;

    @Autowired
    public SearchMusicService(SearchArtistService searchArtistService, SearchAlbumService searchAlbumService) {
        this.searchArtistService = searchArtistService;
        this.searchAlbumService = searchAlbumService;
    }

    public SearchMusicResponse searchMusicResult(String fragment){
        List<ArtistResponse> artists = searchArtistService.findAllArtistStartingWith(fragment);
        List<AlbumResponse> albumResponses = searchAlbumService.findAllAlbumResponseStartingWith(fragment);
        return new SearchMusicResponse(artists, albumResponses);
    }
}
