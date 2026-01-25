package org.musicservice.demo.security.authListener;

import org.musicservice.demo.service.auth.AuthenticationListenerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

// Слушатель успешных аутентификаций
@Component
public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private final AuthenticationListenerService authenticationListenerService;

    @Autowired
    public AuthenticationSuccessListener(AuthenticationListenerService authenticationListenerService) {
        this.authenticationListenerService = authenticationListenerService;
    }

    // Что будет происходить при успешной аутентификации
    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        authenticationListenerService.resetFailedLogin(username);
    }
}
