package org.musicservice.demo.controller.rest.music;

import org.musicservice.demo.dto.likes.LikedContentIds;
import org.musicservice.demo.dto.music.album.AlbumsResponse;
import org.musicservice.demo.dto.music.sound.TracksResponse;
import org.musicservice.demo.service.music.AlbumService;
import org.musicservice.demo.service.music.SoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/tracks")
    public ResponseEntity<TracksResponse> viewTrackCollection(@RequestBody LikedContentIds ids) {
        return ResponseEntity.ok().body(soundService.getTrackCollectionByUserLikes(ids));
    }

    @PostMapping("/albums")
    public ResponseEntity<AlbumsResponse> viewAlbumCollection(@RequestBody LikedContentIds ids) {
        return ResponseEntity.ok().body(albumService.getAlbumCollectionByUserLikes(ids));
    }

}
