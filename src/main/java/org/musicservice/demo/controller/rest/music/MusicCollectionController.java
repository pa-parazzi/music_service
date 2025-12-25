package org.musicservice.demo.controller.rest.music;

import org.musicservice.demo.dto.music.SoundDto;
import org.musicservice.demo.dto.music.response.CollectionTracksResponse;
import org.musicservice.demo.dto.music.response.LikeResponse;
import org.musicservice.demo.service.music.SoundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

@RestController
@RequestMapping("/collection")
public class MusicCollectionController {

    private final SoundService soundService;

    @Autowired
    public MusicCollectionController(SoundService soundService) {
        this.soundService = soundService;
    }

    @PostMapping("/tracks")
    public ResponseEntity<CollectionTracksResponse> viewTrackCollection(@RequestBody List<LikeResponse> responses){
        return ResponseEntity.ok().body(soundService.getTrackCollectionByUserRequest(responses));
    }

}
