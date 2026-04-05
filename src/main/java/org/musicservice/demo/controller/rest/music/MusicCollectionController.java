package org.musicservice.demo.controller.rest.music;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.musicservice.demo.dto.music.album.AlbumResponse;
import org.musicservice.demo.dto.music.common.PageResponse;
import org.musicservice.demo.dto.music.sound.SoundResponse;
import org.musicservice.demo.service.music.AlbumService;
import org.musicservice.demo.service.music.SoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/collection")
public class MusicCollectionController {

    private final SoundService soundService;
    private final AlbumService albumService;

    @Autowired
    public MusicCollectionController(SoundService soundService, AlbumService albumService) {
        this.soundService = soundService;
        this.albumService = albumService;
    }

    @GetMapping("/tracks")
    public ResponseEntity<PageResponse<SoundResponse>> viewTrackCollection(
            @AuthenticationPrincipal Long userId,
            @RequestParam(name = "page") @Min(0) int page,
            @RequestParam(name = "size") @Min(1) @Max(30) int size) {
        return ResponseEntity.ok().body(soundService.getTrackCollectionByUserId(userId, page, size));
    }

    @GetMapping("/albums")
    public ResponseEntity<PageResponse<AlbumResponse>> viewAlbumCollection(
            @AuthenticationPrincipal Long userId,
            @RequestParam (name = "page") @Min(0) int page,
            @RequestParam(name = "size") @Min(1) @Max(30) int size) {
        return ResponseEntity.ok().body(albumService.getAlbumCollectionByUserId(userId, page, size));
    }

}
