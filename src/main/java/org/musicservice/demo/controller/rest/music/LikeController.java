package org.musicservice.demo.controller.rest.music;

import org.musicservice.demo.dto.music.request.LikeRequest;
import org.musicservice.demo.dto.music.request.UserLikesRequest;
import org.musicservice.demo.dto.music.response.LikeResponse;
import org.musicservice.demo.exception.music.LikeNotFoundException;
import org.musicservice.demo.service.music.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/like")
public class LikeController {

    private final LikeService likeService;

    @Autowired
    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping
    public ResponseEntity<LikeResponse> create(@RequestBody LikeRequest request){
        return ResponseEntity.ok().body(likeService.create(request));
    }

    @PostMapping("/delete")
    public ResponseEntity<HttpStatus> dropLike(@RequestBody LikeRequest request){
        likeService.deleteByUserId(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/get")
    public ResponseEntity<List<LikeResponse>> getLikeByRequest(@RequestBody UserLikesRequest request){
        try{
            return ResponseEntity.ok().body(likeService.findAllByUserRequest(request));
        } catch (LikeNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
