package org.musicservice.demo.controller.rest.music;

import org.musicservice.demo.dto.music.genre.GenresResponse;
import org.musicservice.demo.service.music.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/genres")
public class GenreController {

    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public ResponseEntity<GenresResponse> genres(){
        return ResponseEntity.ok().body(genreService.genresNames());
    }
}
