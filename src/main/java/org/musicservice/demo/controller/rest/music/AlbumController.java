package org.musicservice.demo.controller.rest.music;

import org.musicservice.demo.dto.music.mainResponse.AlbumResponse;
import org.musicservice.demo.dto.music.mainResponse.MainResponse;
import org.musicservice.demo.service.music.MusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/album")
public class AlbumController {

    private final MusicService musicService;

    @Autowired
    public AlbumController(MusicService musicService) {
        this.musicService = musicService;
    }

    @GetMapping
    public ResponseEntity<MainResponse> view(){
        return ResponseEntity.ok(musicService.viewAlbums());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlbumResponse> viewById(@PathVariable("id") Long albumId){
        return ResponseEntity.ok(musicService.getById(albumId));
    }
}
