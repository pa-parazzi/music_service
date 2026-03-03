package org.musicservice.demo.support.factory.unit.user;

import org.musicservice.demo.dto.image.ImageResponse;
import org.musicservice.demo.dto.user.LoginRequest;
import org.musicservice.demo.dto.user.RegistrationRequest;
import org.musicservice.demo.dto.user.UserMainResponse;
import org.musicservice.demo.entity.image.UserAvatar;
import org.musicservice.demo.entity.user.Authority;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.security.userDetails.UserPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;


public class UserDataFactory {

    private static final Long ID = 1L;
    private static final String USERNAME = "Alice";
    private static final String PASSWORD = "qwerty12345";
    private static final String EMAIL = "test@gmail.com";
    private static final LocalDate DATE_OF_BIRTH = LocalDate.of(2000, 1, 1);

    private static final String AVATAR_KEY = "default_avatar.jpg";
    private static final String AVATAR_URL = "https://mus-app-img.storage.yandexcloud.net/default_avatar.jpg";

    private static final boolean ACCOUNT_NON_LOCKED = true;
    private static final boolean ENABLED = true;
    private static final Collection<? extends GrantedAuthority> AUTHORITY = List.of(new SimpleGrantedAuthority(Authority.USER.getAuthority()));
    private static final int MAX_FAILED_LOGIN_ATTEMPTS = 3;
    private static final Duration LOCK_DURATION = Duration.ofMinutes(15);

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

    public static RegistrationRequest registrationRequest() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername(USERNAME);
        request.setPassword(PASSWORD);
        request.setEmail(EMAIL);
        request.setDateOfBirth(DATE_OF_BIRTH);
        return request;
    }

    public static LoginRequest loginRequest(){
        LoginRequest request = new LoginRequest();
        request.setUsername(USERNAME);
        request.setPassword(PASSWORD);
        return request;
    }

    public static User user(){
        User user = new User(
                USERNAME,
                PASSWORD,
                EMAIL,
                DATE_OF_BIRTH
        );
        user.setId(ID);
        return user;
    }

    public static UserAvatar userAvatar(User user){
        return new UserAvatar(user, AVATAR_KEY);
    }

    public static ImageResponse avatarResponse(){
        ImageResponse response = new ImageResponse();
        response.setKey(AVATAR_KEY);
        response.setUrl(AVATAR_URL);
        return response;
    }

    public static User userWithFailedLoginAttemptsZero(){
        User user = new User(
                USERNAME,
                PASSWORD,
                EMAIL,
                DATE_OF_BIRTH
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
                DATE_OF_BIRTH
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
                DATE_OF_BIRTH
        );
        user.setId(ID);
        user.setLockTime(Instant.now().plus(LOCK_DURATION));
        user.setFailedLoginAttempts(MAX_FAILED_LOGIN_ATTEMPTS);
        return user;
    }

    public static int maxFailedLoginAttempts(){
        return MAX_FAILED_LOGIN_ATTEMPTS;
    }

    public static Duration lockDurationMinutes(){
        return LOCK_DURATION;
    }

}
