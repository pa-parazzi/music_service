package org.musicservice.demo.controller.admin;

import org.musicservice.demo.service.user.UserService;
import org.musicservice.demo.service.yandexCloud.MusicImportInYandexCloud;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final MusicImportInYandexCloud musicImportInYandexCloud;

    public AdminController(UserService userService, MusicImportInYandexCloud musicImportInYandexCloud) {
        this.userService = userService;
        this.musicImportInYandexCloud = musicImportInYandexCloud;
    }

//    @GetMapping("/main")
//    public UserMainResponse mainMenu(Principal principal){
//        return userService.viewMainResponseById(principal.getName());
//    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadJson() throws Exception {
        try {
            musicImportInYandexCloud.importMusic();
            return ResponseEntity.ok("Импорт запущен");
        } catch (Exception e) {
            throw new Exception("Импорт не удался: " + e.getMessage());
        }
    }
}
