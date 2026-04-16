package org.musicservice.demo.controller.rest.music;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
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
        return ResponseEntity.ok().body(genreService.findAllGenresResponse());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenreResponse> genrePage(@PathVariable("id") Long genreId){
        return ResponseEntity.ok(genreService.genreResponseById(genreId));
    }

    @GetMapping("/{id}/tracks")
    public ResponseEntity<PageResponse<SoundResponse>> pagedArtistsByGenre(
            @PathVariable("id") Long genreId,
            @RequestParam(name = "page") @Min(0) int page,
            @RequestParam(name = "size") @Min(1) @Max(30) int size){
        return ResponseEntity.ok(soundService.getSoundsByGenreIdPaged(genreId, page, size));
    }

    @GetMapping("/{id}/albums")
    public ResponseEntity<PageResponse<AlbumResponse>> pagedAlbumsByGenre(
            @PathVariable("id") Long genreId,
            @RequestParam(name = "page") @Min(0) int page,
            @RequestParam(name = "size") @Min(1) @Max(30) int size){
        return ResponseEntity.ok(albumService.findAlbumsByGenreIdPaged(genreId, page, size));
    }


}
