package org.musicservice.demo.controller.spaNavigation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {

    @GetMapping("/album/{id}")
    public String album(){
        return "forward:/html/pages/album-index.html";
    }

    @GetMapping("/artist/{id}")
    public String artist(){
        return "forward:/html/pages/artist-index.html";
    }

    @GetMapping("/collection/tracks")
    public String collectionTracks(){
        return "forward:/html/pages/collection-tracks.html";
    }

    @GetMapping("/collection/albums")
    public String collectionAlbums(){
        return "forward:/html/pages/collection-albums.html";
    }

    @GetMapping("/genre")
    public String genre(){
        return "forward:/html/pages/genre.html";
    }

    @GetMapping("/genre/{id}")
    public String genreIndex(){
        return "forward:/html/pages/genre-index.html";
    }

    @GetMapping("/sound/{id}")
    public String soundIndex(){
        return "forward:/html/pages/sound-index.html";
    }
}
