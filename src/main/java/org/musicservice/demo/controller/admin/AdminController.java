package org.musicservice.demo.controller.admin;

import org.musicservice.demo.dto.admin.AdminDto;
import org.musicservice.demo.dto.music.MusicDto;
import org.musicservice.demo.service.music.MusicService;
import org.musicservice.demo.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final MusicService musicService;
    private final UserService userService;

    @Autowired
    public AdminController(MusicService musicService, UserService userService) {
        this.musicService = musicService;
        this.userService = userService;
    }


    @GetMapping("/main")
    public AdminDto mainMenu(Principal principal){
        return userService.viewInfoAdmin(principal.getName());
    }

    @PostMapping("/add")
    public ResponseEntity<HttpStatus> addNewArtist(@RequestBody MusicDto musicDto){
        musicService.create(musicDto);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }
}
