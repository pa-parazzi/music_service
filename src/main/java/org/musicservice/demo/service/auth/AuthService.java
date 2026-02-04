package org.musicservice.demo.service.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.musicservice.demo.dto.user.RegistrationRequest;
import org.musicservice.demo.entity.auth.RefreshToken;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.security.dto.TokenResponse;
import org.musicservice.demo.security.dto.TokenSubject;
import org.musicservice.demo.security.dto.VerifyEmailRequest;
import org.musicservice.demo.security.jwt.JwtTokenService;
import org.musicservice.demo.security.refreshToken.RefreshTokenService;
import org.musicservice.demo.security.userDetails.UserDetailsServiceImpl;
import org.musicservice.demo.security.userDetails.UserPrincipal;
import org.musicservice.demo.security.verification.VerificationTokenService;
import org.musicservice.demo.service.image.UserAvatarService;
import org.musicservice.demo.service.user.UserService;
import org.musicservice.demo.service.validator.RegistrationValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final UserService userService;
    private final UserAvatarService avatarService;
    private final RegistrationValidator registrationValidator;
    private final RefreshTokenService refreshTokenService;
    private final VerificationTokenService verificationTokenService;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtTokenService jwtTokenService;

    @Autowired
    public AuthService(UserService userService, UserAvatarService avatarService, RegistrationValidator registrationValidator, RefreshTokenService refreshTokenService, VerificationTokenService verificationTokenService, UserDetailsServiceImpl userDetailsService, JwtTokenService jwtTokenService) {
        this.userService = userService;
        this.avatarService = avatarService;
        this.registrationValidator = registrationValidator;
        this.refreshTokenService = refreshTokenService;
        this.verificationTokenService = verificationTokenService;
        this.userDetailsService = userDetailsService;
        this.jwtTokenService = jwtTokenService;
    }

    @Transactional
    public TokenResponse processRegistration(RegistrationRequest regRequest, MultipartFile file, HttpServletResponse response){
        registrationValidator.validateUsername(regRequest.getUsername());
        registrationValidator.validateEmail(regRequest.getEmail());
        User newUser = userService.create(regRequest);
        Long userId = newUser.getId();
        avatarService.createOrGetDefault(file, newUser);
        VerifyEmailRequest emailRequest = new VerifyEmailRequest(userId, newUser.getEmail());
        verificationTokenService.createToken(emailRequest);
        refreshTokenService.create(userId, response);
        TokenSubject subject = new TokenSubject(userId, List.of(newUser.getRole().getAuthority()));
        String accessToken = jwtTokenService.generateToken(subject);
        return new TokenResponse(accessToken);
    }

    @Transactional
    public TokenResponse processLogin(Authentication authentication, HttpServletResponse response){
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Long userId = principal.userId();
        refreshTokenService.deleteByUserId(userId, response);
        refreshTokenService.create(userId, response);
        TokenSubject subject =  new TokenSubject(userId, principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
        String accessToken = jwtTokenService.generateToken(subject);
        return new TokenResponse(accessToken);
    }

    @Transactional
    public TokenResponse refreshAccess(HttpServletResponse response, HttpServletRequest request){
        RefreshToken foundToken = refreshTokenService.verifyRequest(request);
        refreshTokenService.rotation(foundToken, response);
        Long userId = foundToken.getUserId();
        UserPrincipal principal = userDetailsService.loadPrincipalById(userId);
        TokenSubject tokenSubject =  new TokenSubject(userId, principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
        String accessToken = jwtTokenService.generateToken(tokenSubject);
        return new TokenResponse(accessToken);
    }

}
