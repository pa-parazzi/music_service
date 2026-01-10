package org.musicservice.demo.security.AuthenticationHundler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// Здесь Spring Security ловит все исключения связанные с аутентификацией (логином), эти исключения генерируются внутри Spring Security при логине и до контроллера они не доходят
// AuthenticationFailureHandler - ловит все ошибки связанные с логином пользователя
@Component
public class AuthenticationFailureHandler implements org.springframework.security.web.authentication.AuthenticationFailureHandler {

    private final UserRepository repository;
    private Map<String, String> error;

    @Autowired
    public AuthenticationFailureHandler(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        if(exception instanceof BadCredentialsException){
            error = new HashMap<>();
            error.put("error", "Неверный логин или пароль");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(new ObjectMapper().writeValueAsString(error));
            return;
        }

        if(exception instanceof LockedException){
            String username = request.getAttribute("LOGIN_USERNAME").toString();
            Optional<User> userOptional = repository.searchByUsername(username);
            if (userOptional.isPresent() && !userOptional.get().isAccountNonLocked()) {
                long secondsLeft = userOptional.get().getRemainingLockSeconds();
                error = new HashMap<>();
                error.put("error", "Вы превысили количество попыток входа, попробуйте снова через " + secondsLeft + " минут");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(new ObjectMapper().writeValueAsString(error));
                return;
            }
            return;
        }

        if(exception instanceof DisabledException){
            error = new HashMap<>();
            error.put("error", "Ваш аккаунт не активирован, пожалуйста проверьте почту");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(new ObjectMapper().writeValueAsString(error));
            return;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"error\": \"Ошибка аутентификации\"}");
    }
}