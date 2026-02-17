package org.musicservice.demo.support.factory.user;

import org.musicservice.demo.Authority.Authority;
import org.musicservice.demo.dto.image.UserAvatarResponse;
import org.musicservice.demo.dto.user.LoginRequest;
import org.musicservice.demo.dto.user.RegistrationRequest;
import org.musicservice.demo.dto.user.UserMainResponse;
import org.musicservice.demo.entity.image.UserAvatar;
import org.musicservice.demo.entity.user.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;


public class UserDataFactory {

    private static final Long ID = 1L;
    private static final String USERNAME = "Alice";
    private static final String PASSWORD = "qwerty12345";
    private static final String EMAIL = "igor.bocharov.88@gmail.com";
    private static final LocalDate DATE_OF_BIRTH = LocalDate.of(2000, 1, 1);
    private static final Authority AUTHORITY = Authority.USER;
    private static final String AVATAR_KEY = "default_avatar.jpg";
    private static final String AVATAR_URL = "https://mus-app-img.storage.yandexcloud.net/default_avatar.jpg";

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

    public static User userWithoutIdAndEnabledAccount(PasswordEncoder encoder){
        String password = encodePassword(encoder, PASSWORD);
        User user = new User(USERNAME, password, EMAIL, DATE_OF_BIRTH, AUTHORITY);
        user.setEnabled(true);
        return user;
    }

    public static User userWithoutId(PasswordEncoder encoder){
        String password = encodePassword(encoder, PASSWORD);
        return new User(USERNAME, password, EMAIL, DATE_OF_BIRTH, AUTHORITY);
    }

    public static User userWithUsernameAlreadyExistsByRegistrationRequest(RegistrationRequest request, PasswordEncoder encoder){
        String password = encodePassword(encoder, PASSWORD);
        return new User(
                request.getUsername(),
                password,
                EMAIL,
                DATE_OF_BIRTH,
                AUTHORITY
        );
    }

    public static User userWithEmailAlreadyExistsByRegistrationRequest(RegistrationRequest request, PasswordEncoder encoder){
        String password = encodePassword(encoder, PASSWORD);
        return new User(
                "Alex",
                password,
                request.getEmail(),
                DATE_OF_BIRTH,
                AUTHORITY
        );
    }

    public static User userWithAvatar(){
        User user = new User(
                USERNAME,
                PASSWORD,
                EMAIL,
                DATE_OF_BIRTH,
                AUTHORITY
        );
        user.setId(ID);

        UserAvatar avatar = new UserAvatar(user, AVATAR_KEY);
        avatar.setId(ID);
        user.setUserAvatar(avatar);

        return user;
    }

    public static User user(){
        User user = new User(
                USERNAME,
                PASSWORD,
                EMAIL,
                DATE_OF_BIRTH,
                AUTHORITY
        );
        user.setId(ID);
        return user;
    }

    public static UserMainResponse userMainResponse(User user){
        UserMainResponse userResponse = new UserMainResponse();
        userResponse.setId(user.getId());
        userResponse.setUsername(user.getUsername());
        UserAvatarResponse avatarResponse = new UserAvatarResponse();
        avatarResponse.setKey(user.getUserAvatar().getKey());
        avatarResponse.setUrl(AVATAR_URL);
        userResponse.setAvatar(avatarResponse);
        return userResponse;
    }

    private static String encodePassword(PasswordEncoder encoder, String password){
        return encoder.encode(password);
    }

}
