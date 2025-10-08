package org.musicservice.demo.service.security;

import jakarta.servlet.http.HttpServletResponse;
import org.musicservice.demo.configuration.security.RefreshTokenProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class CookieManager {

    private final RefreshTokenProperties refreshTokenProperties;

    @Autowired
    public CookieManager(RefreshTokenProperties refreshTokenProperties) {
        this.refreshTokenProperties = refreshTokenProperties;
    }

    // Задает refreshToken в cookie в браузере
    public void setCookie(HttpServletResponse response, String refreshToken){
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(refreshTokenProperties.getDuration())
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    // Очистка cookie
    public void clearCookie(HttpServletResponse response){
        ResponseCookie clearCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, clearCookie.toString());
    }
}
