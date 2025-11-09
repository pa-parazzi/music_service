package org.musicservice.demo.util;

import org.musicservice.demo.dto.user.UserDtoForRegistration;
import org.musicservice.demo.exception.AuthException.RegistrationException;
import org.musicservice.demo.mapper.user.UserMapper;
import org.musicservice.demo.model.user.User;
import org.musicservice.demo.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
public class ValidationForRegUser {

    private final UserService userService;

    @Autowired
    public ValidationForRegUser(UserService userService) {
        this.userService = userService;
    }

    public void validate(Object target) {
        UserDtoForRegistration regUser = (UserDtoForRegistration) target;
        Optional<User> findUser = userService.getUserOptionalByUsername(regUser.getUsername());
        if(findUser.isPresent()){
            throw new RegistrationException("Пользователь с таким именем уже существует");
        }
    }
}
