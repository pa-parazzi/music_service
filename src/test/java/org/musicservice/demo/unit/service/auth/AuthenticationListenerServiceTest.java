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
import org.musicservice.demo.support.factory.unit.auth.AuthenticationDataFactory;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;


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
        User user = AuthenticationDataFactory.userWithMaxFailedLoginAttemptsAndNullLockTime();
        String username = user.getUsername();
        int maxFailedAttempts = AuthenticationDataFactory.maxFailedLoginAttempts();
        int lockDurationMinutes = AuthenticationDataFactory.lockDurationMinutes();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(securityProperties.getMaxFailedAttempts()).thenReturn(maxFailedAttempts);
        when(securityProperties.getLockDurationMinutes()).thenReturn(lockDurationMinutes);

        authenticationListenerService.failedLoginProcess(username);

        assertFalse(user.isAccountNonLocked());
        assertTrue(user.getLockTime().isAfter(LocalDateTime.now().plusMinutes(lockDurationMinutes).minusMinutes(1)));
        assertEquals(maxFailedAttempts, user.getFailedLoginAttempts());
    }


    @Test
    void resetFailedLogin_ShouldResetFailedLoginAttemptsAndLockTime_WhenUserIsPresent(){
        User user = AuthenticationDataFactory.userWithMaxFailedLoginAttemptsAndLockTimeWhereLockDurationMinutes();
        String username = user.getUsername();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        authenticationListenerService.resetFailedLogin(username);

        assertEquals(0, user.getFailedLoginAttempts());
        assertNull(user.getLockTime());
    }

    @Test
    void resetFailedLogin_ShouldNothing_WhenUserIsEmpty(){
        User user = AuthenticationDataFactory.userWithMaxFailedLoginAttemptsAndLockTimeWhereLockDurationMinutes();
        int failedAttempts = user.getFailedLoginAttempts();
        int lockDurationOfMinutes = user.getLockTime().getMinute();
        String username = user.getUsername();
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        authenticationListenerService.resetFailedLogin(username);

        assertEquals(failedAttempts, user.getFailedLoginAttempts());
        assertEquals(lockDurationOfMinutes, user.getLockTime().getMinute());
    }


}
