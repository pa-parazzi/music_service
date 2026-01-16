package org.musicservice.demo.controller.rest.music;

import org.musicservice.demo.dto.music.sound.SoundDto;
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

    @GetMapping("/{albumId}")
    public ResponseEntity<List<SoundDto>> getTracksByAlbum(@PathVariable("albumId") Long albumId){
        return ResponseEntity.ok().body(soundService.getSoundListByAlbumId(albumId));
    }
}
