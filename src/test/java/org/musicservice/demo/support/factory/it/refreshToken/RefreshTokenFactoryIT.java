package org.musicservice.demo.support.factory.it.refreshToken;

import org.musicservice.demo.entity.auth.RefreshToken;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.security.properties.RefreshTokenProperties;
import org.musicservice.demo.security.refreshToken.RefreshTokenCryptoService;
import org.musicservice.demo.security.reposiroty.RefreshTokenRepository;

import java.time.Duration;
import java.time.Instant;

public class RefreshTokenFactoryIT {

    public static RefreshToken createNewRefreshToken(String refreshTokenValue, User user, RefreshTokenProperties refreshTokenProperties, RefreshTokenCryptoService refreshTokenCryptoService, RefreshTokenRepository refreshTokenRepository){
        String hash = refreshTokenCryptoService.hash(refreshTokenValue);
        Duration refreshTokenDuration = refreshTokenProperties.getDuration();
        return refreshTokenRepository.save(new RefreshToken(hash, Instant.now().plus(refreshTokenDuration), user.getId()));
    }
}
