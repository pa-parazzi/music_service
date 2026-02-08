package org.musicservice.demo.unit.service.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.repository.user.UserRepository;
import org.musicservice.demo.security.properties.LoginSecurityProperties;
import org.musicservice.demo.service.auth.AuthenticationListenerService;
import org.musicservice.demo.support.factory.auth.AuthenticationDataFactory;
import org.musicservice.demo.support.factory.user.ValidUserDataFactory;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class AuthenticationListenerServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private LoginSecurityProperties securityProperties;

    @InjectMocks
    private AuthenticationListenerService authenticationListenerService;

    @Test
    void failedLoginProcess_ShouldIncrementFailedAttempt(){
        User user = AuthenticationDataFactory.userWithFailedLoginAttemptsZero();
        String username = user.getUsername();
        int maxFailedAttempts = AuthenticationDataFactory.maxFailedLoginAttempts();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(securityProperties.getMaxFailedAttempts()).thenReturn(maxFailedAttempts);

        authenticationListenerService.failedLoginProcess(username);

        assertEquals(1, user.getFailedLoginAttempts());
    }

    @Test
    void failedLoginProcess_ShouldNothing_WhenUserIsEmpty(){
        User user = AuthenticationDataFactory.userWithFailedLoginAttemptsZero();
        String username = user.getUsername();

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        authenticationListenerService.failedLoginProcess(username);

        verifyNoInteractions(securityProperties);
    }

    @Test
    void failedLoginProcess_ShouldSetLockTime_WhenReachedMaxFailedLoginAttempts(){
        User user = AuthenticationDataFactory.userWithMaxFailedLoginAttempts();
        String username = user.getUsername();
        int maxFailedAttempts = AuthenticationDataFactory.maxFailedLoginAttempts();
        int lockDurationMinutes = AuthenticationDataFactory.lockDurationMinutes();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(securityProperties.getMaxFailedAttempts()).thenReturn(maxFailedAttempts);
        when(securityProperties.getLockDurationMinutes()).thenReturn(lockDurationMinutes);

        authenticationListenerService.failedLoginProcess(username);

        assertFalse(user.isAccountNonLocked());
        assertTrue(user.getLockTime().isAfter(LocalDateTime.now().plusMinutes(lockDurationMinutes).minusMinutes(1)));
    }


}
