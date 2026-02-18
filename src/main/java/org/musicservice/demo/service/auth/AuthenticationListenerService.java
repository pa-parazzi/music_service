package org.musicservice.demo.service.auth;

import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.repository.user.UserRepository;
import org.musicservice.demo.security.properties.LoginSecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class AuthenticationListenerService {

    private final UserRepository userRepository;
    private final LoginSecurityProperties securityProperties;

    @Autowired
    public AuthenticationListenerService(UserRepository userRepository, LoginSecurityProperties securityProperties) {
        this.userRepository = userRepository;
        this.securityProperties = securityProperties;
    }

    @Transactional
    public void failedLoginProcess(String username){
        Optional<User> optUser = userRepository.findByUsername(username);
        if(optUser.isEmpty()){
            return;
        }
        User user = optUser.get();
        int failedAttempts = user.getFailedLoginAttempts();
        if(failedAttempts + 1 >= securityProperties.getMaxFailedAttempts()){
            user.setLockTime(Instant.now().plus(securityProperties.getLockDuration()));
            return;
        }
        user.setFailedLoginAttempts(failedAttempts + 1);
    }

    @Transactional
    public void resetFailedLogin(String username){
        Optional<User> optUser = userRepository.findByUsername(username);
        if(optUser.isPresent()){
            User user = optUser.get();
            user.setFailedLoginAttempts(0);
            user.setLockTime(null);
        }
    }

}
