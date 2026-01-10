package org.musicservice.demo.security.cookie;

import jakarta.servlet.http.HttpServletResponse;
import org.musicservice.demo.security.properties.AuthenticationTokenProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieManager {

    private final AuthenticationTokenProperties authenticationTokenProperties;

    @Autowired
    public CookieManager(AuthenticationTokenProperties authenticationTokenProperties) {
        this.authenticationTokenProperties = authenticationTokenProperties;
    }

    // Задает refreshToken в cookie в браузере
    public void setCookie(HttpServletResponse response, String refreshToken){
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(authenticationTokenProperties.getRefreshTokenDuration().plusDays(1).getSeconds())
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    // Очистка cookie
    public void clearCookie(HttpServletResponse response){
        ResponseCookie clearCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, clearCookie.toString());
    }
}
