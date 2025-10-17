package org.musicservice.demo.service.security;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.musicservice.demo.configuration.security.RefreshTokenProperties;
import org.musicservice.demo.dto.user.UserDtoForLogin;
import org.musicservice.demo.exception.refreshTokenError.RefreshTokenNotFoundException;
import org.musicservice.demo.security.jwtAuthentication.cookie.CookieManager;
import org.musicservice.demo.security.jwtAuthentication.cookie.CookieUtil;
import org.musicservice.demo.model.user.RefreshToken;
import org.musicservice.demo.model.user.User;
import org.musicservice.demo.repository.user.RefreshTokenRepository;
import org.musicservice.demo.security.jwtAuthentication.jwt.JWTUtil;
import org.musicservice.demo.security.jwtAuthentication.refreshToken.RefreshTokenUtil;
import org.musicservice.demo.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final CookieManager cookieManager;
    private final RefreshTokenProperties refreshTokenProperties;

    @Autowired
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, CookieManager cookieManager, RefreshTokenProperties refreshTokenProperties) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.cookieManager = cookieManager;
        this.refreshTokenProperties = refreshTokenProperties;
    }

    public RefreshToken searchByTokenHash(String hash) {
        return refreshTokenRepository.findByTokenHash(hash).orElseThrow(()-> new RefreshTokenNotFoundException("refreshToken с таким hash не найден в БД"));
    }

    public Optional<RefreshToken> getOptTokenByHash(String hash){
        return refreshTokenRepository.findByTokenHash(hash);
    }

    @Transactional
    public void createRefreshToken(HttpServletResponse response, User user){
        String generatedRefreshToken = RefreshTokenUtil.generateRefreshToken();
        String hash = RefreshTokenUtil.hash(generatedRefreshToken);
        cookieManager.setCookie(response, generatedRefreshToken);

        Instant expiryDate = Instant.now().plus(refreshTokenProperties.getDuration());
        RefreshToken refreshToken = new RefreshToken(hash, expiryDate, false, user);
        user.setRefreshToken(refreshToken);
        refreshTokenRepository.save(refreshToken);
    }

    // Удаление токена, отвязка от связанной сущности User
    @Transactional
    public void delete(HttpServletRequest request, HttpServletResponse response){
        String refreshTokenByCookie = CookieUtil.getRefreshTokenByCookie(request);
        if(refreshTokenByCookie!=null){
            RefreshToken foundToken = searchByTokenHash(RefreshTokenUtil.hash(refreshTokenByCookie));
            User user = foundToken.getUser();
            user.setRefreshToken(null);
            refreshTokenRepository.delete(foundToken);
        }
        cookieManager.clearCookie(response);
    }

    @Transactional
    public void deleteAllByExpiredSince(Instant now){
        refreshTokenRepository.deleteAllByExpiryDateBefore(now);
    }

    public boolean isExpired(RefreshToken refreshToken){
        return refreshToken.getExpiryDate().isBefore(Instant.now());
    }


}
