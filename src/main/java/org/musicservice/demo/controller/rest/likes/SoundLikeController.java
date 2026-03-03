package org.musicservice.demo.controller.rest.likes;

import org.musicservice.demo.dto.likes.LikedContentIds;
import org.musicservice.demo.security.userDetails.UserPrincipal;
import org.musicservice.demo.service.likes.SoundLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/liked-sounds")
public class SoundLikeController {

    private final SoundLikeService soundLikeService;

    @Autowired
    public SoundLikeController(SoundLikeService soundLikeService) {
        this.soundLikeService = soundLikeService;
    }

    @GetMapping("/get")
    public ResponseEntity<LikedContentIds> getSoundLikes(@AuthenticationPrincipal UserPrincipal principal){
        return ResponseEntity.ok().body(soundLikeService.getAllLikedSounds(principal.userId()));
    }

    @PostMapping("/{soundId}")
    public ResponseEntity<Void> likeSound(@AuthenticationPrincipal UserPrincipal principal, @PathVariable ("soundId") Long soundId){
        soundLikeService.create(principal.userId(), soundId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{soundId}")
    public ResponseEntity<Void> dropLike(@AuthenticationPrincipal UserPrincipal principal, @PathVariable ("soundId") Long soundId){
        soundLikeService.delete(principal.userId(), soundId);
        return ResponseEntity.noContent().build();
    }
}
