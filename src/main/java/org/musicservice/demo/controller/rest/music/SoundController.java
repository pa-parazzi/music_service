package org.musicservice.demo.controller.rest.music;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.musicservice.demo.dto.music.common.PageResponse;
import org.musicservice.demo.dto.music.sound.SoundPageResponse;
import org.musicservice.demo.dto.music.sound.SoundResponse;
import org.musicservice.demo.dto.music.sound.SoundsResponse;
import org.musicservice.demo.service.music.SoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/sound")
public class SoundController {

    private final SoundService soundService;

    @Autowired
    public SoundController(SoundService soundService) {
        this.soundService = soundService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<SoundPageResponse> viewTrack(@PathVariable("id") Long id){
        return ResponseEntity.ok(soundService.getSoundPageResponseById(id));
    }

    @GetMapping("/album/{id}")
    public ResponseEntity<SoundsResponse> getTracksByAlbumPaged(@PathVariable("id") Long id){
        return ResponseEntity.ok().body(soundService.getSoundsByAlbumId(id));
    }

    @GetMapping("/artist/{id}")
    public ResponseEntity<PageResponse<SoundResponse>> getTracksByArtist(
            @PathVariable("id") Long id,
            @RequestParam(name = "page") @Min(0) int page,
            @RequestParam(name = "size") @Min(1) @Max(30) int size){
        return ResponseEntity.ok().body(soundService.getSoundsByArtistIdPaged(id, page, size));
    }
}
