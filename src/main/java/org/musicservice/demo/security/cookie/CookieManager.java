package org.musicservice.demo.security.cookie;

import jakarta.servlet.http.HttpServletResponse;
import org.musicservice.demo.security.properties.RefreshTokenProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieManager {

    private final RefreshTokenProperties refreshTokenProperties;
    private final CookieProperties cookieProperties;

    @Autowired
    public CookieManager(RefreshTokenProperties refreshTokenProperties, CookieProperties cookieProperties) {
        this.refreshTokenProperties = refreshTokenProperties;
        this.cookieProperties = cookieProperties;
    }

    // Задает refreshToken в cookie в браузере
    public void setCookie(HttpServletResponse response, String refreshToken){
        ResponseCookie cookie = ResponseCookie.from(cookieProperties.getRefreshToken(), refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenProperties.getDuration().plusDays(1).getSeconds())
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    // Очистка cookie
    public void clearCookie(HttpServletResponse response){
        ResponseCookie clearCookie = ResponseCookie.from(cookieProperties.getRefreshToken(), "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, clearCookie.toString());
    }
}
