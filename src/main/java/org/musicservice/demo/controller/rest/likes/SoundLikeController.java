package org.musicservice.demo.controller.rest.likes;

import org.musicservice.demo.dto.likes.LikedSounds;
import org.musicservice.demo.dto.likes.UserGetLikesRequest;
import org.musicservice.demo.dto.likes.UserLikedMusicRequest;
import org.musicservice.demo.service.likes.SoundLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/like_sound")
public class SoundLikeController {

    private final SoundLikeService soundLikeService;

    @Autowired
    public SoundLikeController(SoundLikeService soundLikeService) {
        this.soundLikeService = soundLikeService;
    }

    @PostMapping("/get")
    public ResponseEntity<LikedSounds> getSoundLikes(@RequestBody UserGetLikesRequest request){
        return ResponseEntity.ok().body(soundLikeService.getAllLikedSounds(request));
    }

    @PostMapping("/create")
    public ResponseEntity<HttpStatus> likeSound(@RequestBody UserLikedMusicRequest request){
        soundLikeService.create(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<HttpStatus> dropLike(@RequestBody UserLikedMusicRequest request){
        soundLikeService.delete(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
