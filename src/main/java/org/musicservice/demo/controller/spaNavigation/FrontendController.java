package org.musicservice.demo.controller.spaNavigation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {

    @GetMapping("/main")
    public String mainPage(){
        return "forward:/html/pages/main.html";
    }

    @GetMapping("/search/**")
    public String search(){
        return "forward:/html/pages/search.html";
    }

    @GetMapping("/auth/login")
    public String login(){
        return "forward:/html/pages/auth/login.html";
    }

    @GetMapping("/auth/registration")
    public String registration(){
        return "forward:/html/pages/auth/registration.html";
    }

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

    @GetMapping("/genre/**")
    public String genreIndex(){
        return "forward:/html/pages/genre-index.html";
    }

    @GetMapping("/sound/{id}")
    public String soundIndex(){
        return "forward:/html/pages/sound-index.html";
    }
}
