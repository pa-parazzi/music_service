package org.musicservice.demo.security.authListener;

import org.musicservice.demo.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

// ApplicationListener<AuthenticationSuccessEvent> Слушатель успешных аутентификаций
@Component
public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private final UserService service;

    @Autowired
    public AuthenticationSuccessListener(UserService service) {
        this.service = service;

    }

    // Что будет происходить при успешной аутентификации
    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        // Получаем username введенный при логине
        String username = ((UserDetails) event.getAuthentication().getPrincipal()).getUsername();
        // Ищем пользователя в БД по username, если такой пользователь существует, и пароль введен верно - счетчик неудачных логинов сбрасывается до 0
        service.getUserOptionalByUsername(username).ifPresent(service::resetFailedLogin);
    }
}
