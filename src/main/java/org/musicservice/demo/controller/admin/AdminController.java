package org.musicservice.demo.controller.admin;

import org.musicservice.demo.dto.user.UserMainResponse;
import org.musicservice.demo.service.user.UserService;
import org.musicservice.demo.service.yandexCloud.multithreading.UploadData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final UploadData uploadData;

    public AdminController(UserService userService, UploadData uploadData) {
        this.userService = userService;
        this.uploadData = uploadData;
    }

    @GetMapping("/main")
    public UserMainResponse mainMenu(Authentication authentication){
        Long userId = (Long) authentication.getPrincipal();
        return userService.viewMainResponseById(userId);
    }

    @PostMapping("/upload")
    public ResponseEntity<Void> uploadData() throws IOException {
        uploadData.upload();
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
