package org.musicservice.demo.service.auth;

import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.repository.user.UserRepository;
import org.musicservice.demo.security.properties.LoginSecurityProperties;
import org.musicservice.demo.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class FailureAuthService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final LoginSecurityProperties securityProperties;

    @Autowired
    public FailureAuthService(UserService userService, UserRepository userRepository, LoginSecurityProperties securityProperties) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.securityProperties = securityProperties;
    }

    @Transactional
    public void failLogin(String username){
        User user = userService.searchByUsername(username);
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
        if(user.getFailedLoginAttempts()>= securityProperties.getMaxFailedAttempts()){
            user.setLockTime(LocalDateTime.now().plusMinutes(securityProperties.getLockDurationMinutes()));
        }
        userRepository.save(user);
    }

    @Transactional
    public void resetFailedLogin(String username){
        User user = userService.searchByUsername(username);
        user.setFailedLoginAttempts(0);
        user.setLockTime(null);
        userRepository.save(user);
    }

}
