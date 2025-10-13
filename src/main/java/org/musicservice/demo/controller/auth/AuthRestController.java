package org.musicservice.demo.controller.auth;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.musicservice.demo.dto.user.UserDtoForLogin;
import org.musicservice.demo.dto.user.UserDtoForRegistration;
import org.musicservice.demo.exception.AuthenticationHundler.AuthenticationFailureHandlerForUser;
import org.musicservice.demo.model.user.RefreshToken;
import org.musicservice.demo.model.user.User;
import org.musicservice.demo.security.jwtAuthentication.jwt.JWTUtil;
import org.musicservice.demo.security.jwtAuthentication.cookie.CookieManager;
import org.musicservice.demo.service.security.JwtTokenService;
import org.musicservice.demo.service.security.RefreshTokenService;
import org.musicservice.demo.service.security.UserDetailsServiceImpl;
import org.musicservice.demo.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("api/auth")
public class AuthRestController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationFailureHandlerForUser authenticationFailureHandler;
    private final JwtTokenService jwtTokenService;

    @Autowired
    public AuthRestController(UserService userService, AuthenticationManager authenticationManager, JWTUtil jwtUtil, RefreshTokenService refreshTokenService, AuthenticationFailureHandlerForUser authenticationFailureHandler, JwtTokenService jwtTokenService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
        this.authenticationFailureHandler = authenticationFailureHandler;
        this.jwtTokenService = jwtTokenService;
    }

    @PostMapping(value = "/registration", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, String> registration(@RequestPart("user") @Valid UserDtoForRegistration user,
                                            @RequestPart(required = false) MultipartFile avatar,
                                            HttpServletRequest request,
                                            HttpServletResponse response){
        User regUser = userService.registrationUser(user, avatar);
        refreshTokenService.createRefreshToken(response, regUser);
        String jwtToken = jwtTokenService.generateAccessForUser(regUser);
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
