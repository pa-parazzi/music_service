package org.musicservice.demo.service.security;

import org.musicservice.demo.model.user.RefreshToken;
import org.musicservice.demo.repository.user.RefreshTokenRepository;
import org.musicservice.demo.security.token.TokenUtil;
import org.musicservice.demo.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;
    private final Duration duration = Duration.ofDays(30);

    @Autowired
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserService userService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userService = userService;
    }

    @Transactional
    public String createAndPersist(String username){
        String token = TokenUtil.generateRefreshToken();
        String hash = TokenUtil.hash(token);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setTokenHash(hash);
        refreshToken.setUser(userService.searchByUsername(username));
        refreshToken.setRevoked(false);
        refreshToken.setExpiryDate(LocalDateTime.now().plus(duration));
        refreshTokenRepository.save(refreshToken);
        return token;
    }

    @Transactional
    public void revoke(RefreshToken token){
        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }
}
