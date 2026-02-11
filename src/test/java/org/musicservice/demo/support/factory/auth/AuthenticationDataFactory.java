package org.musicservice.demo.support.factory.auth;

import org.musicservice.demo.Authority.Authority;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.security.userDetails.UserPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public class AuthenticationDataFactory {

    private static final Long ID = 1L;
    private static final String USERNAME = "Alice";
    private static final String PASSWORD = "test123";
    private static final boolean ACCOUNT_NON_LOCKED = true;
    private static final boolean ENABLED = true;
    private static final Collection<? extends GrantedAuthority> AUTHORITY = List.of(new SimpleGrantedAuthority(Authority.USER.getAuthority()));

    private static final String EMAIL = "test@mail.com";
    private static final LocalDate DATE_OF_BIRTH = LocalDate.of(2000, 1, 1);

    private static final int MAX_FAILED_LOGIN_ATTEMPTS = 3;
    private static final int LOCK_DURATION_MINUTES = 15;

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

    public static int maxFailedLoginAttempts(){
        return MAX_FAILED_LOGIN_ATTEMPTS;
    }

    public static int lockDurationMinutes(){
        return LOCK_DURATION_MINUTES;
    }

    public static User userWithFailedLoginAttemptsZero(){
        User user = new User(
                USERNAME,
                PASSWORD,
                EMAIL,
                DATE_OF_BIRTH,
                Authority.USER
        );
        user.setId(ID);
        user.setLockTime(null);
        user.setFailedLoginAttempts(0);
        return user;
    }

    public static User userWithMaxFailedLoginAttemptsAndNullLockTime(){
        User user = new User(
                USERNAME,
                PASSWORD,
                EMAIL,
                DATE_OF_BIRTH,
                Authority.USER
        );
        user.setId(ID);
        user.setLockTime(null);
        user.setFailedLoginAttempts(MAX_FAILED_LOGIN_ATTEMPTS);
        return user;
    }

    public static User userWithMaxFailedLoginAttemptsAndLockTimeWhereLockDurationMinutes(){
        User user = new User(
                USERNAME,
                PASSWORD,
                EMAIL,
                DATE_OF_BIRTH,
                Authority.USER
        );
        user.setId(ID);
        user.setLockTime(LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES));
        user.setFailedLoginAttempts(MAX_FAILED_LOGIN_ATTEMPTS);
        return user;
    }

}
