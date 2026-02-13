package org.musicservice.demo.support.factory.auth;

import org.musicservice.demo.entity.auth.RefreshToken;
import org.musicservice.demo.security.refreshToken.RefreshTokenCryptoService;

import java.time.Duration;
import java.time.Instant;

public class RefreshTokenFactory {

    private static final Long USER_ID = 1L;
    private static final Duration DURATION = Duration.ofHours(24);
    private static final Instant EXPIRY_DATE = Instant.now().plus(DURATION);
    private static final Instant EXPIRED_DATE = Instant.now().minusSeconds(10);
    private static final String REFRESH_TOKEN_VALUE = "gGbSy9VGImkRpQLYO9ZgNdzHrWvt0Bjv6uv2jVGwkrUaBbtssxB6OpasGXKqT3Y6qGyMzjU5S3FTPLZ77eMeKg";
    private static final String REFRESH_TOKEN_HASH = "3221641315e7026e76a0bea97ba4bc6a65ad59ff8a5149a6655f1d2fa2b373ae";
    private static final String NEW_REFRESH_TOKEN_HASH = "bd69a30d99e1fead94ceff0aa0a37fc0217d45e1ce3bbc74424b57a6890d41a5";

    public static Long userId(){
        return USER_ID;
    }

    public static String refreshTokenValue(){
        return REFRESH_TOKEN_VALUE;
    }

    public static String newRefreshTokenHash(){
        return NEW_REFRESH_TOKEN_HASH;
    }

    public static Duration duration(){
        return DURATION;
    }

    public static RefreshToken validRefreshToken(){
        return new RefreshToken(REFRESH_TOKEN_HASH, EXPIRY_DATE, USER_ID);
    }

    public static RefreshToken expiredRefreshToken(){
        return new RefreshToken(REFRESH_TOKEN_HASH, EXPIRED_DATE, USER_ID);
    }

    public static String generateNewRefreshTokenValue(RefreshTokenCryptoService service){
        return service.generateRefreshToken();
    }

    public static String hash(RefreshTokenCryptoService service, String value){
        return service.hash(value);
    }
}


