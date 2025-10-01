package org.musicservice.demo.controller.rest.user;

import org.musicservice.demo.dto.user.UserDtoForView;
import org.musicservice.demo.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/lk")
public class UserController {

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/profile")
    public UserDtoForView profile(Principal principal){
        return service.viewSingle(principal.getName());
    }
}
