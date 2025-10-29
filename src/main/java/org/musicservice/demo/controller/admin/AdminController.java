package org.musicservice.demo.controller.admin;

import org.musicservice.demo.dto.user.UserDtoForView;
import org.musicservice.demo.service.readFile.MusicImportService;
import org.musicservice.demo.service.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final MusicImportService musicImportService;

    public AdminController(UserService userService, MusicImportService musicImportService) {
        this.userService = userService;
        this.musicImportService = musicImportService;
    }

    @GetMapping("/main")
    public UserDtoForView mainMenu(Principal principal){
        return userService.viewSingle(principal.getName());
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadJson() throws Exception {
        try {
            musicImportService.importMusic();
            return ResponseEntity.ok("Импорт запущен");
        } catch (Exception e){
            throw new Exception("Импорт не удался: " + e.getMessage());
        }
    }
}
