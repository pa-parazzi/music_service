package org.musicservice.demo.cookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.util.WebUtils;

public final class CookieUtil {

    public static String getRefreshTokenByCookie(HttpServletRequest request){
        Cookie cookie = WebUtils.getCookie(request, "refreshToken");
        if(cookie==null){
            return null;
        }
        return cookie.getValue();
    }
}
