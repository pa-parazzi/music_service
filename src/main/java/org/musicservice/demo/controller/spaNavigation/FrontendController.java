package org.musicservice.demo.controller.spaNavigation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {

    @GetMapping("/album/{id}")
    public String album(){
        return "forward:/music/AlbumIndex.html";
    }

    @GetMapping("/artist/{id}")
    public String artist(){
        return "forward:/music/artistIndex.html";
    }

    @GetMapping("/collection/tracks")
    public String collectionTracks(){
        return "forward:/music/collectionTracks.html";
    }

}
