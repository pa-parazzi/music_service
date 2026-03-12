package org.musicservice.demo.controller.rest.music;

import org.musicservice.demo.dto.music.album.AlbumsResponse;
import org.musicservice.demo.dto.music.artist.ArtistsResponse;
import org.musicservice.demo.dto.music.genre.GenreResponse;
import org.musicservice.demo.dto.music.genre.GenresResponse;
import org.musicservice.demo.service.music.AlbumService;
import org.musicservice.demo.service.music.ArtistService;
import org.musicservice.demo.service.music.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/genre")
public class GenreController {

    private final GenreService genreService;
    private final ArtistService artistService;
    private final AlbumService albumService;

    @Autowired
    public GenreController(GenreService genreService, ArtistService artistService, AlbumService albumService) {
        this.genreService = genreService;
        this.artistService = artistService;
        this.albumService = albumService;
    }

    @GetMapping
    public ResponseEntity<GenresResponse> genres(){
        return ResponseEntity.ok().body(genreService.genresNames());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenreResponse> genrePage(@PathVariable("id") Long genreId){
        return ResponseEntity.ok(genreService.findGenreNameById(genreId));
    }

    @GetMapping("/{id}/artists")
    public ResponseEntity<ArtistsResponse> artistsByGenre(@PathVariable("id") Long genreId){
        return ResponseEntity.ok(artistService.findAllArtistsByGenreId(genreId));
    }

    @GetMapping("/{id}/albums")
    public ResponseEntity<AlbumsResponse> albumsByGenre(@PathVariable("id") Long genreId){
        return ResponseEntity.ok(albumService.findAllAlbumsByGenreId(genreId));
    }


}
