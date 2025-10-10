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


public class AdminController {

    @GetMapping("/main")
    public AdminDto mainMenu(Principal principal){
        return null; //userService.viewInfoAdmin(principal.getName());
    }

    @PostMapping("/add")
    public ResponseEntity<HttpStatus> addNewArtist(@RequestBody MusicDto musicDto){
        //musicService.create(musicDto);
        return null; //ResponseEntity.ok(HttpStatus.ACCEPTED);
    }
}
