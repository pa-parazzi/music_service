package org.musicservice.demo.security.refreshToken;

import org.musicservice.demo.security.reposiroty.RefreshTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class RefreshTokenCleanUpTask {

    private final RefreshTokenRepository repository;

    public RefreshTokenCleanUpTask(RefreshTokenRepository repository) {
        this.repository = repository;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void clearExpiredTokens(){
        repository.deleteALlByExpiryDateBefore(Instant.now());
    }
}
