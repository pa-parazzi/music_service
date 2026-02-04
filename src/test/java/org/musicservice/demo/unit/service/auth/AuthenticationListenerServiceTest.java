package org.musicservice.demo.unit.service.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.musicservice.demo.repository.user.UserRepository;
import org.musicservice.demo.security.properties.LoginSecurityProperties;
import org.musicservice.demo.service.auth.AuthenticationListenerService;
import org.musicservice.demo.support.factory.user.ValidUserDataFactory;

import java.time.LocalDateTime;

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
    void failedLoginProcessTest_ShouldReturnEarly_WhenNoRowsUpdated(){
        String username = ValidUserDataFactory.username();
        int countRowsUpdated = 0;
        when(userRepository.incrementFailedAttempts(username)).thenReturn(countRowsUpdated);

        authenticationListenerService.failedLoginProcess(username);

        verify(userRepository).incrementFailedAttempts(username);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(securityProperties);
    }

    @Test
    void failedLoginAttempts_ShouldIncrementFailedAttempt(){
        String username = ValidUserDataFactory.username();
        int coundRowsUpdated = 1;
        int lockDurationMinutes = 15;
        int failedLoginAttempts = 3;

        when(userRepository.incrementFailedAttempts(username)).thenReturn(coundRowsUpdated);
        when(securityProperties.getLockDurationMinutes()).thenReturn(lockDurationMinutes);
        when(userRepository.lockUserIfMaxLoginAttempts(eq(username), any(LocalDateTime.class), any(Integer.class))).thenReturn(coundRowsUpdated);
        when(securityProperties.getMaxFailedAttempts()).thenReturn(failedLoginAttempts);

        authenticationListenerService.failedLoginProcess(username);

        verify(userRepository).incrementFailedAttempts(username);
        verify(securityProperties).getLockDurationMinutes();
        verify(userRepository).lockUserIfMaxLoginAttempts(eq(username), any(LocalDateTime.class), any(Integer.class));
        verify(securityProperties).getMaxFailedAttempts();

        verifyNoMoreInteractions(userRepository, securityProperties);
    }


    @Test
    void resetFailedLogin_NoMoreInteractions(){
        String username = ValidUserDataFactory.username();

        when(userRepository.resetFailedLoginAttempts(username)).thenReturn(1);

        authenticationListenerService.resetFailedLogin(username);

        verify(userRepository).resetFailedLoginAttempts(username);
        verifyNoMoreInteractions(userRepository);
    }
}
