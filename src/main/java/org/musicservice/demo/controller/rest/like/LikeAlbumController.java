package org.musicservice.demo.controller.rest.like;

import org.musicservice.demo.dto.like.LikedAlbumResponse;
import org.musicservice.demo.dto.like.UserGetLikesRequest;
import org.musicservice.demo.dto.like.UserLikeRequest;
import org.musicservice.demo.service.like.LikeAlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/album/like")
public class LikeAlbumController {

    private final LikeAlbumService likeAlbumService;

    @Autowired
    public LikeAlbumController(LikeAlbumService likeAlbumService) {
        this.likeAlbumService = likeAlbumService;
    }

    @PostMapping("/get")
    public ResponseEntity<List<LikedAlbumResponse>> getLikes(@RequestBody UserGetLikesRequest request){
        return ResponseEntity.ok().body(likeAlbumService.getAllLikedAlbums(request));
    }

    @PostMapping("/create")
    public ResponseEntity<HttpStatus> likeAlbum(@RequestBody UserLikeRequest request){
        likeAlbumService.create(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<HttpStatus> dropLike(@RequestBody UserLikeRequest request){
        likeAlbumService.delete(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
