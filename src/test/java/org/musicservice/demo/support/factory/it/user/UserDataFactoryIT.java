package org.musicservice.demo.support.factory.it.user;

import org.musicservice.demo.Authority.Authority;
import org.musicservice.demo.dto.user.LoginRequest;
import org.musicservice.demo.dto.user.RegistrationRequest;
import org.musicservice.demo.entity.user.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

public class UserDataFactoryIT {

    public static RegistrationRequest registrationRequest() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("username");
        request.setPassword("password");
        request.setEmail("test@mail.com");
        request.setDateOfBirth(LocalDate.of(1997, 2,4));
        return request;
    }

    public static LoginRequest loginRequest(){
        LoginRequest request = new LoginRequest();
        request.setUsername("USERNAME");
        request.setPassword("PASSWORD");
        return request;
    }

    public static User userWithoutIdAndEnabledAccount(PasswordEncoder encoder){
        String password = encodePassword(encoder, "PASSWORD");
        User user = new User("USERNAME", password, "EMAIL", LocalDate.of(1997, 2,4), Authority.USER);
        user.setEnabled(true);
        return user;
    }

    public static User userWithUsernameAlreadyExistsByRegistrationRequest(RegistrationRequest request, PasswordEncoder encoder){
        String password = encodePassword(encoder, "PASSWORD");
        return new User(
                request.getUsername(),
                password,
                "EMAIL",
                LocalDate.of(1997, 2,4),
                Authority.USER
        );
    }

    public static User userWithEmailAlreadyExistsByRegistrationRequest(RegistrationRequest request, PasswordEncoder encoder){
        String password = encodePassword(encoder, "PASSWORD");
        return new User(
                "Alex",
                password,
                request.getEmail(),
                LocalDate.of(1997, 2,4),
                Authority.USER
        );
    }

    public static User userWithoutId(PasswordEncoder encoder){
        String password = encodePassword(encoder, "PASSWORD");
        return new User("USERNAME", password, "EMAIL", LocalDate.of(1997, 2,4), Authority.USER);
    }

    private static String encodePassword(PasswordEncoder encoder, String password){
        return encoder.encode(password);
    }
}
