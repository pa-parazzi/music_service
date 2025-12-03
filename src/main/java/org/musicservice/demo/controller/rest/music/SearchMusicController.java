package org.musicservice.demo.controller.rest.music;

import org.musicservice.demo.dto.music.mainResponse.AlbumResponse;
import org.musicservice.demo.service.music.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchMusicController {

    private final AlbumService albumService;

    @Autowired
    public SearchMusicController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @PostMapping
    public ResponseEntity<List<AlbumResponse>> searchStartingWith(@RequestParam (value = "fragment" ,required = false) String fragment){
        return ResponseEntity.ok(albumService.findAlbumResponseStartingWith(fragment));
    }
}
