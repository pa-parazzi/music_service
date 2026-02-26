package org.musicservice.demo.support.factory.it.user;

import org.musicservice.demo.dto.user.LoginRequest;
import org.musicservice.demo.dto.user.RegistrationRequest;
import org.musicservice.demo.entity.image.UserAvatar;
import org.musicservice.demo.entity.user.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.UUID;

public class UserDataFactoryIT {

    private static final LocalDate DATE_OF_BIRTH = LocalDate.of(1997, 2,4);

    public static RegistrationRequest registrationRequest() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("username123");
        request.setPassword("test5t65");
        request.setEmail( "test@gmail.com");
        request.setDateOfBirth(DATE_OF_BIRTH);
        return request;
    }

    public static LoginRequest loginRequest(String username, String password){
        LoginRequest request = new LoginRequest();
        request.setUsername(username);
        request.setPassword(password);
        return request;
    }

    public static User userWithEnabledAccount(PasswordEncoder encoder){
        String password = encodePassword(encoder, UUID.randomUUID().toString());
        User user = new User(UUID.randomUUID().toString(), password, UUID.randomUUID().toString() + "@gmail.com", DATE_OF_BIRTH);
        user.setEnabled(true);
        return user;
    }

    public static User userWithUsernameAlreadyExistsByRegistrationRequest(RegistrationRequest request, PasswordEncoder encoder){
        String password = encodePassword(encoder, UUID.randomUUID().toString());
        return new User(
                request.getUsername(),
                password,
                UUID.randomUUID().toString() + "@gmail.com",
                DATE_OF_BIRTH
        );
    }

    public static User userWithEmailAlreadyExistsByRegistrationRequest(RegistrationRequest request, PasswordEncoder encoder){
        String password = encodePassword(encoder, UUID.randomUUID().toString());
        return new User(
                UUID.randomUUID().toString(),
                password,
                request.getEmail(),
                DATE_OF_BIRTH
        );
    }

    public static User userWithEncodedPassword(PasswordEncoder encoder){
        String password = encodePassword(encoder, UUID.randomUUID().toString());
        return new User(UUID.randomUUID().toString(), password, UUID.randomUUID().toString() + "@gmail.com", DATE_OF_BIRTH);
    }

    public static User user(){
        return new User(UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString() + "@gmail.com", DATE_OF_BIRTH);
    }

    public static UserAvatar userAvatar(User user){
        return new UserAvatar(user, UUID.randomUUID().toString() + ".jpg");
    }

    private static String encodePassword(PasswordEncoder encoder, String password){
        return encoder.encode(password);
    }
}
