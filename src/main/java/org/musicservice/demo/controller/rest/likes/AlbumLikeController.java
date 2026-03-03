package org.musicservice.demo.controller.rest.likes;

import org.musicservice.demo.dto.likes.LikedContentIds;
import org.musicservice.demo.security.userDetails.UserPrincipal;
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

    @GetMapping("/get")
    public ResponseEntity<LikedContentIds> getLikes(@AuthenticationPrincipal UserPrincipal principal){
        return ResponseEntity.ok().body(albumLikeService.getAllLikedAlbums(principal.userId()));
    }

    @PostMapping("/{albumId}")
    public ResponseEntity<Void> likeAlbum(@AuthenticationPrincipal UserPrincipal principal, @PathVariable ("albumId") Long albumId){
        albumLikeService.create(principal.userId(), albumId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{albumId}")
    public ResponseEntity<Void> dropLike(@AuthenticationPrincipal UserPrincipal principal, @PathVariable ("albumId") Long albumId){
        albumLikeService.delete(principal.userId(), albumId);
        return ResponseEntity.noContent().build();
    }
}
