package org.musicservice.demo.factory.user;

import org.musicservice.demo.Authority.Authority;
import org.musicservice.demo.model.user.User;
import org.musicservice.demo.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;

import java.time.LocalDate;

@TestConfiguration
public class UserDataFactory {

    @Autowired
    private UserRepository userRepository;

    public User createUserFactory(){
        User user = new User();
        user.setUsername("TestUser");
        user.setPassword("test123");
        user.setEmail("igor.bocharov.88@gmail.com");
        LocalDate date = LocalDate.of(2001, 1, 1);
        user.setDateOfBirth(date);
        user.setFailedLoginAttempts(0);
        user.setLockTime(null);
        user.setEnabled(false);
        user.setRole(Authority.USER);
        return user;
    }

    public User save(User user){
        return userRepository.save(user);
    }

    public void cleanUser(){
        userRepository.deleteAll();
    }
}
