package org.musicservice.demo.security.refreshToken;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.musicservice.demo.Authority.Authority;
import org.musicservice.demo.security.properties.AuthenticationTokenProperties;
import org.musicservice.demo.security.exception.RefreshTokenNotFoundException;
import org.musicservice.demo.entity.auth.RefreshToken;
import org.musicservice.demo.security.reposiroty.RefreshTokenRepository;
import org.musicservice.demo.security.cookie.CookieManager;
import org.musicservice.demo.security.cookie.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final CookieManager cookieManager;
    private final AuthenticationTokenProperties authenticationTokenProperties;

    @Autowired
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, CookieManager cookieManager, AuthenticationTokenProperties authenticationTokenProperties) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.cookieManager = cookieManager;
        this.authenticationTokenProperties = authenticationTokenProperties;
    }

    public RefreshToken searchByTokenHash(String hash) {
        return refreshTokenRepository.findEntityByTokenHash(hash).orElseThrow(()-> new RefreshTokenNotFoundException("refreshToken с таким hash не найден в БД"));
    }

    public Optional<RefreshTokenProjection> getOptTokenByHash(String hash){
        return refreshTokenRepository.findForAuthByTokenHash(hash);
    }

    @Transactional
    public void create(HttpServletResponse response, Long userId, Authority role){
        String generatedRefreshToken = RefreshTokenUtil.generateRefreshToken();
        String hash = RefreshTokenUtil.hash(generatedRefreshToken);
        cookieManager.setCookie(response, generatedRefreshToken);
        Instant expiryDate = Instant.now().plus(authenticationTokenProperties.getRefreshTokenDuration());
        RefreshToken refreshToken = new RefreshToken(hash, expiryDate, userId, role);
        refreshTokenRepository.save(refreshToken);
    }

    // Удаление токена
    @Transactional
    public void delete(HttpServletRequest request, HttpServletResponse response){
        String refreshTokenByCookie = CookieUtil.getRefreshTokenByCookie(request);
        if(refreshTokenByCookie!=null){
            RefreshToken foundToken = searchByTokenHash(RefreshTokenUtil.hash(refreshTokenByCookie));
            refreshTokenRepository.delete(foundToken);
            cookieManager.clearCookie(response);
        }
    }

    public boolean isExpired(Instant expiryDate){
        return expiryDate.isBefore(Instant.now());
    }

}
