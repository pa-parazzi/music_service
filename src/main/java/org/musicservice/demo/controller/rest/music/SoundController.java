package org.musicservice.demo.controller.rest.music;

import org.musicservice.demo.dto.music.sound.TracksResponse;
import org.musicservice.demo.service.music.SoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sound")
public class SoundController {

    private final SoundService soundService;

    @Autowired
    public SoundController(SoundService soundService) {
        this.soundService = soundService;
    }

    @GetMapping("/album/{id}")
    public ResponseEntity<TracksResponse> getTracksByAlbum(@PathVariable("id") Long id){
        return ResponseEntity.ok().body(soundService.getSoundListByAlbumId(id));
    }

    @GetMapping("/artist/{id}")
    public ResponseEntity<TracksResponse> getTracksByArtist(@PathVariable("id") Long id){
        return ResponseEntity.ok().body(soundService.getSoundListByArtistId(id));
    }
}
