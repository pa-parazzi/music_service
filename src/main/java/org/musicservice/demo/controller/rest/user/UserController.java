package org.musicservice.demo.controller.rest.user;

import org.musicservice.demo.dto.user.UserMainResponse;
import org.musicservice.demo.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/profile")
    public UserMainResponse profile(@AuthenticationPrincipal Long userId){
        return service.mainResponse(userId);
    }
}
