package org.musicservice.demo.controller.admin;

import org.musicservice.demo.dto.admin.AdminDto;
import org.musicservice.demo.dto.music.MusicDto;
import org.musicservice.demo.dto.music.MusicInsertDto;
import org.musicservice.demo.dto.user.UserDtoForView;
import org.musicservice.demo.service.music.MusicService;
import org.musicservice.demo.service.readFile.MusicReaderManager;
import org.musicservice.demo.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final MusicService musicService;

    public AdminController(UserService userService, MusicService musicService) {
        this.userService = userService;
        this.musicService = musicService;
    }

    @GetMapping("/main")
    public UserDtoForView mainMenu(Principal principal){
        return userService.viewSingle(principal.getName());
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadJson(@RequestParam("file")MultipartFile file){
        try{
            musicService.importFile(file);
            return ResponseEntity.ok("Успешная обработка файла");
        } catch (IOException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ошибка чтения файла" + e.getMessage());
        }
    }
}
