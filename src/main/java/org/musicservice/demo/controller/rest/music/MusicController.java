package org.musicservice.demo.controller.rest.music;

import org.apache.coyote.Response;
import org.musicservice.demo.dto.music.AlbumDto;
import org.musicservice.demo.dto.music.SoundDto;
import org.musicservice.demo.dto.music.mainResponse.MainResponse;
import org.musicservice.demo.service.music.MusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MusicController {

    private final MusicService musicService;

    @Autowired
    public MusicController(MusicService musicService) {
        this.musicService = musicService;
    }

    @GetMapping("/music")
    public ResponseEntity<MainResponse> view(){
        return ResponseEntity.ok(musicService.viewMusic());
    }
}
