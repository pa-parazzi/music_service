package org.musicservice.demo.controller.rest.music;

import org.musicservice.demo.dto.music.search.SearchMusicResponse;
import org.musicservice.demo.service.search.SearchMusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
public class SearchMusicController {

    private final SearchMusicService searchMusicService;

    @Autowired
    public SearchMusicController(SearchMusicService searchMusicService) {
        this.searchMusicService = searchMusicService;
    }

    @GetMapping("/{fragment}")
    public ResponseEntity<SearchMusicResponse> searchStartingWith(@PathVariable (value = "fragment", required = false) String fragment){
        return ResponseEntity.ok(searchMusicService.searchMusicResult(fragment));
    }
}
