package org.musicservice.demo.service.security;

import org.musicservice.demo.repository.user.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@Transactional(readOnly = true)
public class RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final Duration duration = Duration.ofDays(30);

    @Autowired
    public RefreshTokenService(RefreshTokenRepository repository) {
        this.repository = repository;
    }

    public String createAndPersist(){
        return null; // TODO: Доделать
    }
}
