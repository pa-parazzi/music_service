package org.musicservice.demo.controller.rest.music;

import org.musicservice.demo.dto.music.search.SearchMusicResponse;
import org.musicservice.demo.service.search.SearchMusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
public class SearchMusicController {

    private final SearchMusicService searchMusicService;

    @Autowired
    public SearchMusicController(SearchMusicService searchMusicService) {
        this.searchMusicService = searchMusicService;
    }

    @PostMapping
    public ResponseEntity<SearchMusicResponse> searchStartingWith(@RequestParam (value = "fragment", required = false) String fragment){
        return ResponseEntity.ok(searchMusicService.searchMusicResult(fragment));
    }
}
