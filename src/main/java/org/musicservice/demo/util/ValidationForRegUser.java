package org.musicservice.demo.util;

import org.musicservice.demo.dto.user.RegistrationRequest;
import org.musicservice.demo.security.exception.RegistrationException;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ValidationForRegUser {

    private final UserService userService;

    @Autowired
    public ValidationForRegUser(UserService userService) {
        this.userService = userService;
    }

    public void validate(Object target) {
        RegistrationRequest regUser = (RegistrationRequest) target;
        Optional<User> findUser = userService.findOptByUsername(regUser.getUsername());
        if(findUser.isPresent()){
            throw new RegistrationException("Пользователь с таким именем уже существует");
        }
    }
}
