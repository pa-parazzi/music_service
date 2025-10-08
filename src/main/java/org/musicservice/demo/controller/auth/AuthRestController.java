package org.musicservice.demo.controller.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.musicservice.demo.dto.user.UserDtoForLogin;
import org.musicservice.demo.dto.user.UserDtoForRegistration;
import org.musicservice.demo.exception.AuthenticationHundler.AuthenticationFailureHandlerForUser;
import org.musicservice.demo.model.user.RefreshToken;
import org.musicservice.demo.security.token.JWTUtil;
import org.musicservice.demo.service.security.RefreshTokenService;
import org.musicservice.demo.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("api/auth")
public class AuthRestController {

    private final UserService service;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationFailureHandlerForUser authenticationFailureHandler;

    @Autowired
    public AuthRestController(UserService service, AuthenticationManager authenticationManager, JWTUtil jwtUtil, RefreshTokenService refreshTokenService, AuthenticationFailureHandlerForUser authenticationFailureHandler) {
        this.service = service;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    @PostMapping(value = "/registration", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, String> registration(@RequestPart("user") @Valid UserDtoForRegistration user,
                                            @RequestPart(required = false) MultipartFile avatar,
                                            HttpServletRequest request,
                                            HttpServletResponse response){
        service.registrationUser(user, avatar);
        refreshTokenService.createRefreshTokenFromUser(request, response, user.getUsername());
        String jwtToken = jwtUtil.generateToken(user.getUsername());
        return Map.of("jwt_token", jwtToken);
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody @Valid UserDtoForLogin userDtoForLogin,
                                     HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            request.setAttribute("LOGIN_USERNAME", userDtoForLogin.getUsername());
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDtoForLogin.getUsername(), userDtoForLogin.getPassword());
            authenticationManager.authenticate(authenticationToken);
            RefreshToken refreshToken = refreshTokenService.createRefreshTokenFromUser(request, response, userDtoForLogin.getUsername());
            String jwtToken = refreshTokenService.generateJwtFromLogin(refreshToken);
            return Map.of("jwt_token", jwtToken);
        } catch (AuthenticationException e){
            authenticationFailureHandler.onAuthenticationFailure(request, response, e);
            return null;
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response){
        refreshTokenService.delete(request);
        refreshTokenService.clearCookie(response);
        return ResponseEntity.ok("Вы вышли из аккаутна");
    }

    @PostMapping("/refresh")
    public Map<String, String> generateNewAccessToken(HttpServletRequest request) {
        String jwtToken = refreshTokenService.refreshJwtToken(request);
        return Map.of("jwt_token", jwtToken);
    }

}
