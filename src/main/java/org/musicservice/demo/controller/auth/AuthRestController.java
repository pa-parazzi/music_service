package org.musicservice.demo.controller.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.musicservice.demo.dto.user.LoginRequest;
import org.musicservice.demo.dto.user.RegistrationRequest;
import org.musicservice.demo.security.AuthenticationHundler.AuthenticationFailureHandler;
import org.musicservice.demo.security.cookie.CookieUtil;
import org.musicservice.demo.security.dto.TokenSubject;
import org.musicservice.demo.security.jwt.JwtTokenService;
import org.musicservice.demo.security.refreshToken.RefreshTokenProjection;
import org.musicservice.demo.security.refreshToken.RefreshTokenService;
import org.musicservice.demo.security.refreshToken.RefreshTokenUtil;
import org.musicservice.demo.security.userDetails.UserDetailsServiceImpl;
import org.musicservice.demo.security.userDetails.UserPrincipal;
import org.musicservice.demo.service.auth.AuthService;
import org.musicservice.demo.util.ValidationForRegUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    private final AuthService authService;
    private final ValidationForRegUser validationForRegUser;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationFailureHandler authenticationFailureHandler;
    private final JwtTokenService jwtTokenService;
    private final UserDetailsServiceImpl userDetailsService;


    @Autowired
    public AuthRestController(AuthService authService, ValidationForRegUser validationForRegUser, AuthenticationManager authenticationManager, RefreshTokenService refreshTokenService, AuthenticationFailureHandler authenticationFailureHandler, JwtTokenService jwtTokenService, UserDetailsServiceImpl userDetailsService) {
        this.authService = authService;
        this.validationForRegUser = validationForRegUser;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
        this.authenticationFailureHandler = authenticationFailureHandler;
        this.jwtTokenService = jwtTokenService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping(value = "/registration", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, String> registration(@RequestPart("user") @Valid RegistrationRequest regRequest,
                                            @RequestPart(required = false) MultipartFile file,
                                            HttpServletResponse response){
        validationForRegUser.validate(regRequest);
        TokenSubject tokenSubject = authService.processRegistration(regRequest, file, response);
        String jwtToken = jwtTokenService.generateToken(tokenSubject);
        return Map.of("jwt_token", jwtToken);
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody @Valid LoginRequest loginRequest,
                                     HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            request.setAttribute("LOGIN_USERNAME", loginRequest.getUsername());
            Authentication authentication = authenticationManager.authenticate
                    (new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            TokenSubject tokenSubject = authService.processLogin(authentication, response);
            String jwtToken = jwtTokenService.generateToken(tokenSubject);
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

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(HttpServletResponse response, HttpServletRequest request){
        String refreshTokenByCookie = CookieUtil.getRefreshTokenByCookie(request);
        if (refreshTokenByCookie != null) {
            String hash = RefreshTokenUtil.hash(refreshTokenByCookie);
            Optional<RefreshTokenProjection> foundToken = refreshTokenService.getOptTokenByHash(hash);
            if (foundToken.isPresent()) {
                RefreshTokenProjection refreshToken = foundToken.get();
                if (refreshTokenService.isExpired(refreshToken.getExpiryDate())) {
                    refreshTokenService.delete(request, response);
                    refreshTokenService.create(response, refreshToken.getUserId());
                }
                UserPrincipal principal = userDetailsService.loadPrincipalById(refreshToken.getUserId());
                TokenSubject tokenSubject = new TokenSubject(principal.userId(), principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
                String jwtToken = jwtTokenService.generateToken(tokenSubject);
                return ResponseEntity.ok(Map.of("accessToken", jwtToken));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }


}
