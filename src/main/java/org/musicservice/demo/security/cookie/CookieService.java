package org.musicservice.demo.security.cookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

@Component
public class CookieService {

    private final CookieProperties cookieProperties;

    @Autowired
    public CookieService(CookieProperties cookieProperties) {
        this.cookieProperties = cookieProperties;
    }

    // Получаем refresh-token из cookie
    public String getRefreshTokenByCookie(HttpServletRequest request){
        Cookie cookie = WebUtils.getCookie(request, cookieProperties.getRefreshToken());
        if(cookie==null){
            return null;
        }
        return cookie.getValue();
    }
}
