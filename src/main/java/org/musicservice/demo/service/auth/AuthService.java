package org.musicservice.demo.service.auth;

import jakarta.servlet.http.HttpServletResponse;
import org.musicservice.demo.dto.user.RegistrationRequest;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.security.dto.TokenSubject;
import org.musicservice.demo.security.dto.VerifyEmailRequest;
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

    @Autowired
    public AuthService(UserService userService, UserAvatarService avatarService, RefreshTokenService refreshTokenService, VerificationTokenService verificationTokenService) {
        this.userService = userService;
        this.avatarService = avatarService;
        this.refreshTokenService = refreshTokenService;
        this.verificationTokenService = verificationTokenService;
    }

    @Transactional
    public TokenSubject processRegistration(RegistrationRequest regRequest, MultipartFile file, HttpServletResponse response){
        User regUser = userService.create(regRequest);
        avatarService.createOrGet(file, regUser);
        VerifyEmailRequest emailRequest = new VerifyEmailRequest();
        emailRequest.setUserId(regUser.getId());
        emailRequest.setEmail(regUser.getEmail());
        verificationTokenService.createToken(emailRequest);
        refreshTokenService.create(response, regUser.getId());
        return TokenSubjectMapper.from(regUser);
    }

    @Transactional
    public TokenSubject processLogin(Authentication authentication, HttpServletResponse response){
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Long userId = principal.userId();
        refreshTokenService.deleteByUserId(userId, response);
        refreshTokenService.create(response ,userId);
        return new TokenSubject(userId, principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
    }

}
