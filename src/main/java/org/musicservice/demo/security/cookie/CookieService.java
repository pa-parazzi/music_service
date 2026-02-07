package org.musicservice.demo.security.cookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

@Component
public class CookieService {

    // Получаем refresh-token из cookie
    public String getRefreshTokenByCookie(HttpServletRequest request){
        Cookie cookie = WebUtils.getCookie(request, "refreshToken");
        if(cookie==null){
            return null;
        }
        return cookie.getValue();
    }
}
