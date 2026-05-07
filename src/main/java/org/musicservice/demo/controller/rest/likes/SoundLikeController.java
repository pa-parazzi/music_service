package org.musicservice.demo.controller.rest.likes;

import org.musicservice.demo.dto.likes.LikeStatusResponse;
import org.musicservice.demo.dto.likes.LikedContentIds;
import org.musicservice.demo.service.likes.SoundLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/private/sound-like")
public class SoundLikeController {

    private final SoundLikeService soundLikeService;

    @Autowired
    public SoundLikeController(SoundLikeService soundLikeService) {
        this.soundLikeService = soundLikeService;
    }

    @GetMapping
    public ResponseEntity<LikedContentIds> getAllLikedSoundsByUserId(@AuthenticationPrincipal Long userId){
        return ResponseEntity.ok().body(soundLikeService.findAllLikedSoundIds(userId));
    }

    @GetMapping("/is-liked/{id}")
    public ResponseEntity<LikeStatusResponse> statusIsLikedSound(@AuthenticationPrincipal Long userId,
                                                                 @PathVariable ("id") Long soundId){
        return ResponseEntity.ok(soundLikeService.findLikedSound(userId, soundId));
    }

    @PostMapping("/{id}")
    public ResponseEntity<Void> likeSound(@AuthenticationPrincipal Long userId, @PathVariable ("id") Long soundId){
        soundLikeService.create(userId, soundId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> dropLike(@AuthenticationPrincipal Long userId, @PathVariable ("id") Long soundId){
        soundLikeService.delete(userId, soundId);
        return ResponseEntity.noContent().build();
    }
}
