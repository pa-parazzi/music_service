package org.musicservice.demo.controller.rest.music;

import org.musicservice.demo.dto.music.sound.SoundResponse;
import org.musicservice.demo.service.music.SoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sound")
public class SoundController {

    private final SoundService soundService;

    @Autowired
    public SoundController(SoundService soundService) {
        this.soundService = soundService;
    }

    @GetMapping("/album/{id}")
    public ResponseEntity<List<SoundResponse>> getTracksByAlbum(@PathVariable("id") Long id){
        return ResponseEntity.ok().body(soundService.getSoundListByAlbumId(id));
    }

    @GetMapping("/artist/{id}")
    public ResponseEntity<List<SoundResponse>> getTracksByArtist(@PathVariable("id") Long id){
        return ResponseEntity.ok().body(soundService.getSoundListByArtistId(id));
    }
}
