package org.musicservice.demo.controller.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.musicservice.demo.dto.user.LoginRequest;
import org.musicservice.demo.dto.user.RegistrationRequest;
import org.musicservice.demo.security.dto.TokenResponse;
import org.musicservice.demo.security.refreshToken.RefreshTokenService;
import org.musicservice.demo.security.verificationToken.VerificationTokenService;
import org.musicservice.demo.service.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final VerificationTokenService verificationTokenService;

    @Autowired
    public AuthRestController(AuthService authService, RefreshTokenService refreshTokenService, VerificationTokenService verificationTokenService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.verificationTokenService = verificationTokenService;
    }

    @PostMapping(value = "/registration", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TokenResponse> registration(@Valid @RequestPart("user") RegistrationRequest regRequest,
                                            @RequestPart(name = "file", required = false) MultipartFile file,
                                            HttpServletResponse response){
        return ResponseEntity.ok(authService.processRegistration(regRequest, file, response));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest loginRequest,
                                               HttpServletResponse response) {
            return ResponseEntity.ok(authService.processLogin(loginRequest, response));
    }

    @PostMapping("/logout")
    public ResponseEntity<HttpStatus> logout(HttpServletRequest request, HttpServletResponse response){
        refreshTokenService.dropToken(request, response);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(HttpServletResponse response, HttpServletRequest request){
        return ResponseEntity.ok(authService.refreshAccess(response, request));
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activate(@RequestParam(required = false, value = "token") String token){
        return ResponseEntity.ok(verificationTokenService.verify(token));
    }

}
