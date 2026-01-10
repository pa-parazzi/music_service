package org.musicservice.demo.controller.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.musicservice.demo.dto.user.LoginRequest;
import org.musicservice.demo.dto.user.RegistrationRequest;
import org.musicservice.demo.security.AuthenticationHundler.AuthenticationFailureHandler;
import org.musicservice.demo.security.refreshToken.RefreshTokenService;
import org.musicservice.demo.service.auth.AuthService;
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

    private final AuthService authService;
    private final ValidationForRegUser validationForRegUser;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    public AuthRestController(AuthService authService, ValidationForRegUser validationForRegUser, AuthenticationManager authenticationManager, RefreshTokenService refreshTokenService, AuthenticationFailureHandler authenticationFailureHandler) {
        this.authService = authService;
        this.validationForRegUser = validationForRegUser;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    @PostMapping(value = "/registration", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, String> registration(@RequestPart("user") @Valid RegistrationRequest regRequest,
                                            @RequestPart(required = false) MultipartFile file,
                                            HttpServletResponse response){
        validationForRegUser.validate(regRequest);
        String jwtToken = authService.processRegistration(regRequest, file, response);
        return Map.of("jwt_token", jwtToken);
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody @Valid LoginRequest loginRequest,
                                     HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            request.setAttribute("LOGIN_USERNAME", loginRequest.getUsername());
            Authentication authentication = authenticationManager.authenticate
                    (new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            String jwtToken = authService.processLogin(authentication);
            return Map.of("jwt_token", jwtToken);
        } catch (AuthenticationException e){
            authenticationFailureHandler.onAuthenticationFailure(request, response, e);
            return null;
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<HttpStatus> logout(HttpServletRequest request, HttpServletResponse response){
        refreshTokenService.delete(request, response);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
