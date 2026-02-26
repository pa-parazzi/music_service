package org.musicservice.demo.security.refreshToken;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
public class RefreshTokenCleanUpTask {

    private final RefreshTokenRepository repository;

    public RefreshTokenCleanUpTask(RefreshTokenRepository repository) {
        this.repository = repository;
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void clearExpiredTokens(){
        repository.deleteAllByExpiryDateBefore(Instant.now());
    }
}
