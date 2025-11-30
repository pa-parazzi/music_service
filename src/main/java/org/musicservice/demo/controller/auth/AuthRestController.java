package org.musicservice.demo.controller.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.musicservice.demo.dto.user.UserDtoForLogin;
import org.musicservice.demo.dto.user.UserDtoForRegistration;
import org.musicservice.demo.exception.AuthenticationHundler.AuthenticationFailureHandlerForUser;
import org.musicservice.demo.model.user.User;
import org.musicservice.demo.service.security.JwtTokenService;
import org.musicservice.demo.service.security.RefreshTokenService;
import org.musicservice.demo.service.user.UserService;
import org.musicservice.demo.util.ValidationForRegUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    private final UserService userService;
    private final ValidationForRegUser validationForRegUser;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationFailureHandlerForUser authenticationFailureHandler;

    @Autowired
    public AuthRestController(UserService userService, ValidationForRegUser validationForRegUser, AuthenticationManager authenticationManager, RefreshTokenService refreshTokenService, AuthenticationFailureHandlerForUser authenticationFailureHandler) {
        this.userService = userService;
        this.validationForRegUser = validationForRegUser;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    @PostMapping(value = "/registration", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, String> registration(@RequestPart("user") @Valid UserDtoForRegistration user,
                                            @RequestPart(required = false) MultipartFile avatar,
                                            HttpServletResponse response){
        validationForRegUser.validate(user);
        String jwtToken = userService.processRegistrationUser(user, avatar, response);
        return Map.of("jwt_token", jwtToken);
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody @Valid UserDtoForLogin userDtoForLogin,
                                     HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            request.setAttribute("LOGIN_USERNAME", userDtoForLogin.getUsername());
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDtoForLogin.getUsername(), userDtoForLogin.getPassword());
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            String jwtToken = userService.processLogin(request, response, authentication.getName());
            return Map.of("jwt_token", jwtToken);
        } catch (AuthenticationException e){
            authenticationFailureHandler.onAuthenticationFailure(request, response, e);
            return null;
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response){
        refreshTokenService.delete(request, response);
        return ResponseEntity.ok("Вы вышли из аккаутна");
    }

}
