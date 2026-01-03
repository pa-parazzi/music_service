package org.musicservice.demo.controller.rest.music;

import org.musicservice.demo.dto.like.LikedAlbumResponse;
import org.musicservice.demo.dto.like.LikedSoundResponse;
import org.musicservice.demo.dto.music.album.CollectionAlbumsResponse;
import org.musicservice.demo.dto.music.sound.CollectionTracksResponse;
import org.musicservice.demo.service.music.AlbumService;
import org.musicservice.demo.service.music.SoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/collection")
public class MusicCollectionController {

    private final SoundService soundService;
    private final AlbumService albumService;

    @Autowired
    public MusicCollectionController(SoundService soundService, AlbumService albumService) {
        this.soundService = soundService;
        this.albumService = albumService;
    }

    @PostMapping("/tracks")
    public ResponseEntity<CollectionTracksResponse> viewTrackCollection(@RequestBody List<LikedSoundResponse> responses) {
        return ResponseEntity.ok().body(soundService.getTrackCollectionByUserLikes(responses));
    }

    @PostMapping("/albums")
    public ResponseEntity<CollectionAlbumsResponse> viewAlbumCollection(@RequestBody List<LikedAlbumResponse> responses) {
        return ResponseEntity.ok().body(albumService.getAlbumCollectionByUserLikes(responses));
    }

}
