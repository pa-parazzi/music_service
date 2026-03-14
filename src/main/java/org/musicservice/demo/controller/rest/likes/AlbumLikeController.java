package org.musicservice.demo.controller.rest.likes;

import org.musicservice.demo.dto.likes.LikeStatusResponse;
import org.musicservice.demo.dto.likes.LikedContentIds;
import org.musicservice.demo.service.likes.AlbumLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/liked-albums")
public class AlbumLikeController {

    private final AlbumLikeService albumLikeService;

    @Autowired
    public AlbumLikeController(AlbumLikeService albumLikeService) {
        this.albumLikeService = albumLikeService;
    }

    @GetMapping
    public ResponseEntity<LikedContentIds> getLikes(@AuthenticationPrincipal Long userId){
        return ResponseEntity.ok().body(albumLikeService.getAllLikedAlbums(userId));
    }

    @GetMapping("/is-liked/{id}")
    public ResponseEntity<LikeStatusResponse> statusIsLikedAlbum(@AuthenticationPrincipal Long userId,
                                                                 @PathVariable ("id") Long albumId){
        return ResponseEntity.ok(albumLikeService.findLikedAlbum(userId, albumId));
    }

    @PostMapping("/{albumId}")
    public ResponseEntity<Void> likeAlbum(@AuthenticationPrincipal Long userId, @PathVariable ("albumId") Long albumId){
        albumLikeService.create(userId, albumId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{albumId}")
    public ResponseEntity<Void> dropLike(@AuthenticationPrincipal Long userId, @PathVariable ("albumId") Long albumId){
        albumLikeService.delete(userId, albumId);
        return ResponseEntity.noContent().build();
    }
}
