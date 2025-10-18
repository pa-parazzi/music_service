package org.musicservice.demo.security.jwtAuthentication.refreshToken;

import org.musicservice.demo.service.security.RefreshTokenService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class RefreshTokenCleanUpTask {

    private final RefreshTokenService refreshTokenService;

    public RefreshTokenCleanUpTask(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void clearExpiredTokens(){
        refreshTokenService.deleteAllByExpiredSince(Instant.now());
    }
}
