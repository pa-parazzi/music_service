package org.musicservice.demo.service.auth;

import jakarta.servlet.http.HttpServletResponse;
import org.musicservice.demo.dto.user.RegistrationRequest;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.security.dto.TokenSubject;
import org.musicservice.demo.security.jwt.JwtTokenService;
import org.musicservice.demo.security.refreshToken.RefreshTokenService;
import org.musicservice.demo.security.userDetails.UserPrincipal;
import org.musicservice.demo.security.util.TokenSubjectMapper;
import org.musicservice.demo.security.verification.VerificationTokenService;
import org.musicservice.demo.service.image.UserAvatarService;
import org.musicservice.demo.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final UserService userService;
    private final UserAvatarService avatarService;
    private final RefreshTokenService refreshTokenService;
    private final VerificationTokenService verificationTokenService;
    private final JwtTokenService jwtTokenService;

    @Autowired
    public AuthService(UserService userService, UserAvatarService avatarService, RefreshTokenService refreshTokenService, VerificationTokenService verificationTokenService, JwtTokenService jwtTokenService) {
        this.userService = userService;
        this.avatarService = avatarService;
        this.refreshTokenService = refreshTokenService;
        this.verificationTokenService = verificationTokenService;
        this.jwtTokenService = jwtTokenService;
    }

    @Transactional
    public String processRegistration(RegistrationRequest regRequest, MultipartFile file, HttpServletResponse response){
        User regUser = userService.create(regRequest);
        avatarService.createOrGet(file, regUser);
        verificationTokenService.createToken(regUser);
        refreshTokenService.create(response, regUser.getId(), regUser.getRole());
        TokenSubject tokenSubject = TokenSubjectMapper.from(regUser);
        return jwtTokenService.generateToken(tokenSubject);
    }

    // Логин, выдача jwt после успешной аутентификации
    @Transactional
    public String processLogin(Authentication authentication){
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        TokenSubject subject = new TokenSubject(principal.userId(), principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
        return jwtTokenService.generateToken(subject);
    }

}
