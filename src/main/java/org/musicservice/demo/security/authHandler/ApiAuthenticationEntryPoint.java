package org.musicservice.demo.security.authHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.musicservice.demo.exception.response.ApiErrorResponse;
import org.musicservice.demo.exception.response.AuthErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ApiAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Autowired
    public ApiAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        AuthErrorCode errorCode = switch (authException) {
            case BadCredentialsException e -> AuthErrorCode.BAD_CREDENTIALS;
            case UsernameNotFoundException e -> AuthErrorCode.BAD_CREDENTIALS;
            case LockedException e -> AuthErrorCode.ACCOUNT_LOCKED;
            case DisabledException e -> AuthErrorCode.ACCOUNT_NOT_ACTIVATED;
            default -> AuthErrorCode.BAD_AUTHENTICATION_REQUEST;
        };

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        ApiErrorResponse body = new ApiErrorResponse(
                errorCode.name(),
                errorCode.getMessage(),
                status.value(),
                System.currentTimeMillis(),
                null
        );

        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
