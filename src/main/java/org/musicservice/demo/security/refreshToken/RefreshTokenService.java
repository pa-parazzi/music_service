package org.musicservice.demo.security.refreshToken;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.musicservice.demo.entity.auth.RefreshToken;
import org.musicservice.demo.exception.response.RefreshTokenErrorCode;
import org.musicservice.demo.security.cookie.CookieManager;
import org.musicservice.demo.security.cookie.CookieUtil;
import org.musicservice.demo.exception.VerifyRefreshTokenException;
import org.musicservice.demo.security.properties.RefreshTokenProperties;
import org.musicservice.demo.security.reposiroty.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
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

    public RefreshToken verifyRequest(HttpServletRequest request) {
        String refreshTokenValue = CookieUtil.getRefreshTokenByCookie(request);
        if(refreshTokenValue==null){
            throw new VerifyRefreshTokenException(RefreshTokenErrorCode.MISSING);
        }
        String hash = RefreshTokenUtil.hash(refreshTokenValue);
        return refreshTokenRepository.findByTokenHash(hash).orElseThrow(()-> new VerifyRefreshTokenException(RefreshTokenErrorCode.INVALID));
    }

    @Transactional
    public void rotation(RefreshToken refreshToken, HttpServletResponse response){
        if(isExpired(refreshToken.getExpiryDate())){
            Long userId = refreshToken.getUserId();
            String newToken = RefreshTokenUtil.generateRefreshToken();
            String hash = RefreshTokenUtil.hash(newToken);
            Instant expiryDate = Instant.now().plus(refreshTokenProperties.getDuration());
            refreshTokenRepository.rotation(userId, hash, expiryDate);
            cookieManager.setCookie(response, newToken);
        }
    }

    @Transactional
    public void dropToken(HttpServletRequest request, HttpServletResponse response){
        String refreshTokenValue = CookieUtil.getRefreshTokenByCookie(request);
        if(refreshTokenValue==null){
            return;
        }
        String hash = RefreshTokenUtil.hash(refreshTokenValue);
        Optional<RefreshToken> optRefreshToken = refreshTokenRepository.findByTokenHash(hash);
        optRefreshToken.ifPresent(refreshTokenRepository::delete);
        cookieManager.clearCookie(response);
    }

    @Transactional
    public void deleteByUserId(Long userId, HttpServletResponse response){
        refreshTokenRepository.deleteByUserId(userId);
        cookieManager.clearCookie(response);
    }

    @Transactional
    public RefreshToken create(Long userId, HttpServletResponse response){
        String generatedRefreshToken = RefreshTokenUtil.generateRefreshToken();
        String hash = RefreshTokenUtil.hash(generatedRefreshToken);
        cookieManager.setCookie(response, generatedRefreshToken);
        Instant expiryDate = Instant.now().plus(refreshTokenProperties.getDuration());
        return refreshTokenRepository.save(new RefreshToken(hash, expiryDate, userId));
    }

    public boolean isExpired(Instant expiryDate){
        return expiryDate.isBefore(Instant.now());
    }

}
