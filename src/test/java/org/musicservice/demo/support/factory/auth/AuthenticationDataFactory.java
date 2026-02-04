package org.musicservice.demo.support.factory.auth;

import org.musicservice.demo.Authority.Authority;
import org.musicservice.demo.entity.auth.RefreshToken;
import org.musicservice.demo.security.refreshToken.RefreshTokenUtil;
import org.musicservice.demo.security.userDetails.UserPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;

public class AuthenticationDataFactory {

    private static final Long ID = 1L;
    private static final String USERNAME = "Alice";
    private static final String PASSWORD = "test123";
    private static final boolean ACCOUNT_NON_LOCKED = true;
    private static final boolean ENABLED = true;
    private static final Collection<? extends GrantedAuthority> AUTHORITY = List.of(new SimpleGrantedAuthority(Authority.USER.getAuthority()));

    private static final String ACCESS_TOKEN = "jwt-token";

    public static UserPrincipal principal(){
        return new UserPrincipal(
                ID,
                USERNAME,
                PASSWORD,
                ACCOUNT_NON_LOCKED,
                ENABLED,
                AUTHORITY);
    }

    public static Authentication authentication(UserPrincipal principal){
        return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    }

    public static String accessToken(){
        return ACCESS_TOKEN;
    }

    public static RefreshToken refreshToken(Long userid){
        String tokenValue = RefreshTokenUtil.generateRefreshToken();
        String hash = RefreshTokenUtil.hash(tokenValue);
        Duration duration = Duration.ofHours(24);
        Instant expiryDate = Instant.now().plus(duration);
        return new RefreshToken(hash, expiryDate, userid);
    }

}
