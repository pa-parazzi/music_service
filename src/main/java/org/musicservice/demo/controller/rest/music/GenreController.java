package org.musicservice.demo.controller.rest.music;

import org.musicservice.demo.dto.music.album.AlbumResponse;
import org.musicservice.demo.dto.music.common.PageResponse;
import org.musicservice.demo.dto.music.genre.GenreResponse;
import org.musicservice.demo.dto.music.genre.GenresResponse;
import org.musicservice.demo.dto.music.sound.SoundResponse;
import org.musicservice.demo.service.music.AlbumService;
import org.musicservice.demo.service.music.GenreService;
import org.musicservice.demo.service.music.SoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/genre")
public class GenreController {

    private final GenreService genreService;
    private final SoundService soundService;
    private final AlbumService albumService;

    @Autowired
    public GenreController(GenreService genreService, SoundService soundService, AlbumService albumService) {
        this.genreService = genreService;
        this.soundService = soundService;
        this.albumService = albumService;
    }

    @GetMapping
    public ResponseEntity<GenresResponse> genres(){
        return ResponseEntity.ok().body(genreService.genresNames());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenreResponse> genrePage(@PathVariable("id") Long genreId){
        return ResponseEntity.ok(genreService.genreResponseById(genreId));
    }

    @GetMapping("/{id}/tracks")
    public ResponseEntity<PageResponse<SoundResponse>> pagedArtistsByGenre(
            @PathVariable("id") Long genreId,
            @RequestParam(name = "page") int page,
            @RequestParam(name = "size") int size){
        return ResponseEntity.ok(soundService.findTracksByGenreIdPaged(genreId, page, size));
    }

    @GetMapping("/{id}/albums")
    public ResponseEntity<PageResponse<AlbumResponse>> pagedAlbumsByGenre(
            @PathVariable("id") Long genreId,
            @RequestParam(name = "page") int page,
            @RequestParam(name = "size") int size){
        return ResponseEntity.ok(albumService.findAlbumsByGenreIdPaged(genreId, page, size));
    }


}
