package org.musicservice.demo.controller.rest.music;

import org.musicservice.demo.dto.music.album.AlbumResponse;
import org.musicservice.demo.dto.music.artist.ArtistResponse;
import org.musicservice.demo.dto.music.common.PageResponse;
import org.musicservice.demo.dto.music.sound.SoundResponse;
import org.musicservice.demo.service.search.SearchMusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
public class SearchMusicController {

    private final SearchMusicService searchMusicService;

    @Autowired
    public SearchMusicController(SearchMusicService searchMusicService) {
        this.searchMusicService = searchMusicService;
    }

    @GetMapping("/{fragment}/artists")
    public ResponseEntity<PageResponse<ArtistResponse>> foundArtistsView(
            @PathVariable (value = "fragment") String fragment,
            @RequestParam(name = "page") int page,
            @RequestParam(name = "size") int size){
        return ResponseEntity.ok(searchMusicService.getArtistsByNameStartingWith(fragment, page, size));
    }

    @GetMapping("/{fragment}/albums")
    public ResponseEntity<PageResponse<AlbumResponse>> foundAlbumsView(
            @PathVariable (value = "fragment") String fragment,
            @RequestParam(name = "page") int page,
            @RequestParam(name = "size") int size){
        return ResponseEntity.ok(searchMusicService.getAlbumsByTitleStartingWith(fragment, page, size));
    }

    @GetMapping("/{fragment}/tracks")
    public ResponseEntity<PageResponse<SoundResponse>> foundTracksView(
            @PathVariable (value = "fragment") String fragment,
            @RequestParam(name = "page") int page,
            @RequestParam(name = "size") int size){
        return ResponseEntity.ok(searchMusicService.getTracksByTitleStartingWith(fragment, page, size));
    }
}
