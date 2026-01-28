package org.musicservice.demo.security.authListener;

import org.musicservice.demo.service.auth.AuthenticationListenerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

// Слушатель событий связанных с неудачной аутентификацией пользователя
@Component
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    private final AuthenticationListenerService authenticationListenerService;

    @Autowired
    public AuthenticationFailureListener(AuthenticationListenerService authenticationListenerService) {
        this.authenticationListenerService = authenticationListenerService;
    }

    // Реализация, что будет происходить при неудачной аутентификации
    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        String username = event.getAuthentication().getName();
        authenticationListenerService.failedLoginProcess(username);
    }
}
