package org.musicservice.demo.security.authListener;

import org.musicservice.demo.service.auth.FailureAuthService;
import org.musicservice.demo.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

// ApplicationListener<AuthenticationFailureBadCredentialsEvent> - Слушатель событий связанных с неудачной аутентификацией пользователя
@Component
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    private final UserService userService;
    private final FailureAuthService failureAuthService;

    @Autowired
    public AuthenticationFailureListener(UserService userService, FailureAuthService failureAuthService) {
        this.userService = userService;
        this.failureAuthService = failureAuthService;
    }

    // Реализация, что будет происходить при неудачной аутентификации
    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        // Достаем username из объекта аутентификации
        String username = event.getAuthentication().getPrincipal().toString();
        // Ищем пользователя в БД по username введенный при логине, если пользователь с таким логином существует, но пароль не верный - растет счетчик неверных логинов
        userService.findOptByUsername(username).ifPresent(failureAuthService::failLogin);
    }
}
