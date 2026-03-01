package org.musicservice.demo.controller.rest.likes;

import org.musicservice.demo.dto.likes.LikedSounds;
import org.musicservice.demo.service.likes.SoundLikeService;
import org.musicservice.demo.annotations.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<LikedSounds> getSoundLikes(@CurrentUser Long userId){
        return ResponseEntity.ok().body(soundLikeService.getAllLikedSounds(userId));
    }

    @PostMapping("/{soundId}")
    public ResponseEntity<Void> likeSound(@CurrentUser Long userId, @PathVariable ("soundId") Long soundId){
        soundLikeService.create(userId, soundId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{soundId}")
    public ResponseEntity<Void> dropLike(@CurrentUser Long userId, @PathVariable ("soundId") Long soundId){
        soundLikeService.delete(userId, soundId);
        return ResponseEntity.noContent().build();
    }
}
