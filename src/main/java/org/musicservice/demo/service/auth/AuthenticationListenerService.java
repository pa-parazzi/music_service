package org.musicservice.demo.service.auth;

import org.musicservice.demo.repository.user.UserRepository;
import org.musicservice.demo.security.properties.LoginSecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
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
        int updateRows = userRepository.incrementFailedAttempts(username);
        if(updateRows == 0){
            return;
        }
        LocalDateTime lockTime = LocalDateTime.now().plusMinutes(securityProperties.getLockDurationMinutes());
        userRepository.lockUserIfMaxLoginAttempts(username, lockTime, securityProperties.getMaxFailedAttempts());
    }

    @Transactional
    public void resetFailedLogin(String username){
        userRepository.resetFailedLoginAttempts(username);
    }

}
