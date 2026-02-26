package org.musicservice.demo.controller.rest.like;

import org.musicservice.demo.dto.like.LikedAlbums;
import org.musicservice.demo.dto.like.UserGetLikesRequest;
import org.musicservice.demo.dto.like.UserLikedMusicRequest;
import org.musicservice.demo.service.like.LikeAlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/like_album")
public class LikeAlbumController {

    private final LikeAlbumService likeAlbumService;

    @Autowired
    public LikeAlbumController(LikeAlbumService likeAlbumService) {
        this.likeAlbumService = likeAlbumService;
    }

    @PostMapping("/get")
    public ResponseEntity<LikedAlbums> getLikes(@RequestBody UserGetLikesRequest request){
        return ResponseEntity.ok().body(likeAlbumService.getAllLikedAlbums(request));
    }

    @PostMapping("/create")
    public ResponseEntity<HttpStatus> likeAlbum(@RequestBody UserLikedMusicRequest request){
        likeAlbumService.create(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<HttpStatus> dropLike(@RequestBody UserLikedMusicRequest request){
        likeAlbumService.delete(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
