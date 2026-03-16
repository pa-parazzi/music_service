package org.musicservice.demo.controller.admin;

import org.musicservice.demo.dto.user.UserMainResponse;
import org.musicservice.demo.service.uploadData.MusicImportService;
import org.musicservice.demo.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final MusicImportService musicImportService;

    @Autowired
    public AdminController(MusicImportService musicImportService) {
        this.musicImportService = musicImportService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Void> uploadData(@RequestParam ("genreName") String genreName) {
        musicImportService.uploadData(genreName);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
