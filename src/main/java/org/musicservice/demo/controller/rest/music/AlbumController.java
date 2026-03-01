package org.musicservice.demo.controller.rest.music;

import org.musicservice.demo.dto.music.album.AlbumListResponse;
import org.musicservice.demo.dto.music.album.AlbumResponse;
import org.musicservice.demo.service.music.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/album")
public class AlbumController {

    private final AlbumService albumService;

    @Autowired
    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping
    public ResponseEntity<AlbumListResponse> view(){
        return ResponseEntity.ok(albumService.getAllAlbumsByMainResponse());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlbumResponse> viewById(@PathVariable("id") Long albumId){
        return ResponseEntity.ok(albumService.findByIdWithArtistAndImage(albumId));
    }
}
