package org.musicservice.demo.security.authListener;

import org.musicservice.demo.service.auth.FailureAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

// Слушатель событий связанных с неудачной аутентификацией пользователя
@Component
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    private final FailureAuthService failureAuthService;

    @Autowired
    public AuthenticationFailureListener(FailureAuthService failureAuthService) {
        this.failureAuthService = failureAuthService;
    }

    // Реализация, что будет происходить при неудачной аутентификации
    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        String username = event.getAuthentication().getName();
        failureAuthService.failLogin(username);
    }
}
