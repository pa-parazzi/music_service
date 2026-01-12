package org.musicservice.demo.security.verification;

import org.musicservice.demo.security.reposiroty.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
public class VerificationTokenCleanUpTask {

    private final VerificationTokenRepository repository;

    @Autowired
    public VerificationTokenCleanUpTask(VerificationTokenRepository repository) {
        this.repository = repository;
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cleanExpiredTokens(){
        repository.deleteAllByExpiryDateBefore(Instant.now());
    }
}
