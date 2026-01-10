package org.musicservice.demo.security.authListener;

import org.musicservice.demo.security.userDetails.UserPrincipal;
import org.musicservice.demo.service.auth.FailureAuthService;
import org.musicservice.demo.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

// ApplicationListener<AuthenticationSuccessEvent> Слушатель успешных аутентификаций
@Component
public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private final UserService userService;
    private final FailureAuthService failureAuthService;

    @Autowired
    public AuthenticationSuccessListener(UserService userService, FailureAuthService failureAuthService) {
        this.userService = userService;
        this.failureAuthService = failureAuthService;
    }

    // Что будет происходить при успешной аутентификации
    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        // Получаем username введенный при логине
        String username = ((UserPrincipal) event.getAuthentication().getPrincipal()).getUsername();
        // Ищем пользователя в БД по username, если такой пользователь существует, и пароль введен верно - счетчик неудачных логинов сбрасывается до 0
        //userService.findOptByUsername(username).ifPresent(failureAuthService::resetFailedLogin);
    }
}
