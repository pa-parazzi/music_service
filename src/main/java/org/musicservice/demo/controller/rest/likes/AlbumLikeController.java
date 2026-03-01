package org.musicservice.demo.controller.rest.likes;

import org.musicservice.demo.dto.likes.LikedAlbums;
import org.musicservice.demo.dto.likes.UserGetLikesRequest;
import org.musicservice.demo.dto.likes.UserLikedMusicRequest;
import org.musicservice.demo.service.likes.AlbumLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/like_album")
public class AlbumLikeController {

    private final AlbumLikeService albumLikeService;

    @Autowired
    public AlbumLikeController(AlbumLikeService albumLikeService) {
        this.albumLikeService = albumLikeService;
    }

    @PostMapping("/get")
    public ResponseEntity<LikedAlbums> getLikes(@RequestBody UserGetLikesRequest request){
        return ResponseEntity.ok().body(albumLikeService.getAllLikedAlbums(request));
    }

    @PostMapping("/create")
    public ResponseEntity<HttpStatus> likeAlbum(@RequestBody UserLikedMusicRequest request){
        albumLikeService.create(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<HttpStatus> dropLike(@RequestBody UserLikedMusicRequest request){
        albumLikeService.delete(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
