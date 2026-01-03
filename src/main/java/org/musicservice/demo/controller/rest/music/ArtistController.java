package org.musicservice.demo.controller.rest.music;

import org.musicservice.demo.dto.music.artist.ArtistResponse;
import org.musicservice.demo.service.music.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/artist")
public class ArtistController {

    private final ArtistService artistService;

    @Autowired
    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtistResponse> viewArtist(@PathVariable("id") Long artistId){
        return ResponseEntity.ok(artistService.viewArtistById(artistId));
    }
}
