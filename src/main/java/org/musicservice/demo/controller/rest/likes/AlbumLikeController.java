package org.musicservice.demo.controller.rest.likes;

import org.musicservice.demo.dto.likes.LikeStatusResponse;
import org.musicservice.demo.service.likes.AlbumLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/album-like")
public class AlbumLikeController {

    private final AlbumLikeService albumLikeService;

    @Autowired
    public AlbumLikeController(AlbumLikeService albumLikeService) {
        this.albumLikeService = albumLikeService;
    }

    @GetMapping("/is-liked/{id}")
    public ResponseEntity<LikeStatusResponse> statusIsLikedAlbum(@AuthenticationPrincipal Long userId,
                                                                 @PathVariable ("id") Long albumId){
        return ResponseEntity.ok(albumLikeService.findLikedAlbum(userId, albumId));
    }

    @PostMapping("/{id}")
    public ResponseEntity<Void> likeAlbum(@AuthenticationPrincipal Long userId, @PathVariable ("id") Long albumId){
        albumLikeService.create(userId, albumId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> dropLike(@AuthenticationPrincipal Long userId, @PathVariable ("id") Long albumId){
        albumLikeService.delete(userId, albumId);
        return ResponseEntity.noContent().build();
    }
}
