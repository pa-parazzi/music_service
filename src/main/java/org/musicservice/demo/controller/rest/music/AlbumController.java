package org.musicservice.demo.controller.rest.music;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.musicservice.demo.dto.music.album.AlbumResponse;
import org.musicservice.demo.dto.music.common.PageResponse;
import org.musicservice.demo.service.music.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/album")
public class AlbumController {

    private final AlbumService albumService;

    @Autowired
    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlbumResponse> viewById(@PathVariable("id") Long albumId){
        return ResponseEntity.ok(albumService.findByIdWithArtistAndImage(albumId));
    }

    @GetMapping("/releases")
    public ResponseEntity<PageResponse<AlbumResponse>> albumReleases(
            @RequestParam(name = "page") @Min(0) int page,
            @RequestParam(name = "size") @Min(1) @Max(30) int size){
        return ResponseEntity.ok(albumService.getNewAlbumReleases(page, size));
    }
}
