package org.musicservice.demo.controller.rest.like;

import org.musicservice.demo.dto.like.LikedSounds;
import org.musicservice.demo.dto.like.UserGetLikesRequest;
import org.musicservice.demo.dto.like.UserLikeRequest;
import org.musicservice.demo.service.like.LikeSoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sound/like")
public class LikeSoundController {

    private final LikeSoundService likeSoundService;

    @Autowired
    public LikeSoundController(LikeSoundService likeSoundService) {
        this.likeSoundService = likeSoundService;
    }

    @PostMapping("/get")
    public ResponseEntity<LikedSounds> getSoundLikes(@RequestBody UserGetLikesRequest request){
        return ResponseEntity.ok().body(likeSoundService.getAllLikedSounds(request));
    }

    @PostMapping("/create")
    public ResponseEntity<HttpStatus> likeSound(@RequestBody UserLikeRequest request){
        likeSoundService.create(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<HttpStatus> dropLike(@RequestBody UserLikeRequest request){
        likeSoundService.delete(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
