package org.musicservice.demo.controller.rest.music;

import org.musicservice.demo.dto.like.LikedAlbums;
import org.musicservice.demo.dto.like.LikedSounds;
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
    public ResponseEntity<CollectionTracksResponse> viewTrackCollection(@RequestBody LikedSounds likedSounds) {
        return ResponseEntity.ok().body(soundService.getTrackCollectionByUserLikes(likedSounds));
    }

    @PostMapping("/albums")
    public ResponseEntity<CollectionAlbumsResponse> viewAlbumCollection(@RequestBody LikedAlbums albumsIds) {
        return ResponseEntity.ok().body(albumService.getAlbumCollectionByUserLikes(albumsIds));
    }

}
