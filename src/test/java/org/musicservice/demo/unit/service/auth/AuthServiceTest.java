package org.musicservice.demo.unit.service.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.musicservice.demo.dto.user.RegistrationRequest;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.exception.RegistrationException;
import org.musicservice.demo.exception.response.UniqueFieldErrorCode;
import org.musicservice.demo.repository.user.UserRepository;
import org.musicservice.demo.security.dto.TokenResponse;
import org.musicservice.demo.security.dto.TokenSubject;
import org.musicservice.demo.security.dto.VerifyEmailRequest;
import org.musicservice.demo.security.jwt.JwtTokenService;
import org.musicservice.demo.security.refreshToken.RefreshTokenService;
import org.musicservice.demo.security.userDetails.UserDetailsServiceImpl;
import org.musicservice.demo.security.verification.VerificationTokenService;
import org.musicservice.demo.service.auth.AuthService;
import org.musicservice.demo.service.image.UserAvatarService;
import org.musicservice.demo.service.user.UserService;
import org.musicservice.demo.service.validator.RegistrationValidator;
import org.musicservice.demo.support.factory.ValidUserDataFactory;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserAvatarService avatarService;
    @Mock
    private RegistrationValidator registrationValidator;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private VerificationTokenService verificationTokenService;
    @Mock
    private UserDetailsServiceImpl userDetailsService;
    @Mock
    private JwtTokenService jwtTokenService;

    private final MockMultipartFile mockMultipartFile = new MockMultipartFile(
            "avatar",
            "avatar.png",
            "image/png",
            "fake image bytes".getBytes());

    private final MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

    @InjectMocks
    private AuthService authService;

    @Test
    void processRegistrationTest(){
        final RegistrationRequest registrationRequest = ValidUserDataFactory.registrationRequest();
        final User newUser = ValidUserDataFactory.user();
        final String accessToken = "jwt-token";
        when(userService.create(registrationRequest)).thenReturn(newUser);
        when(jwtTokenService.generateToken(any(TokenSubject.class))).thenReturn(accessToken);

        final TokenResponse result = authService.processRegistration(registrationRequest, mockMultipartFile, mockHttpServletResponse);

        assertEquals(accessToken, result.accessToken());

        verify(registrationValidator).validateUsername(registrationRequest.getUsername());
        verify(registrationValidator).validateEmail(registrationRequest.getEmail());
        verify(userService).create(registrationRequest);
        verify(avatarService).createOrGet(eq(mockMultipartFile), eq(newUser));
        verify(verificationTokenService).createToken(any(VerifyEmailRequest.class));
        verify(refreshTokenService).create(eq(mockHttpServletResponse), eq(newUser.getId()));
        verify(jwtTokenService).generateToken(any(TokenSubject.class));
        verifyNoMoreInteractions(registrationValidator, userService, avatarService, verificationTokenService, refreshTokenService, jwtTokenService);
    }


    @Test
    void processRegistration_ShouldThrowRegistrationException_UsernameAlreadyExists(){
        final RegistrationRequest registrationRequest = ValidUserDataFactory.registrationRequest();
        final String username = registrationRequest.getUsername();

        doThrow(new RegistrationException("Validation error", UniqueFieldErrorCode.USERNAME)).when(registrationValidator).validateUsername(username);

        assertThrows(RegistrationException.class, ()-> authService.processRegistration(registrationRequest, mockMultipartFile, mockHttpServletResponse));

        verify(registrationValidator).validateUsername(username);
        verifyNoInteractions(userService, avatarService, verificationTokenService, refreshTokenService, jwtTokenService);
    }


    @Test
    void processRegistration_ShouldThrowRegistrationException_EmailAlreadyExists(){
        final RegistrationRequest registrationRequest = ValidUserDataFactory.registrationRequest();
        final String email = registrationRequest.getEmail();

        doThrow(new RegistrationException("Validation error", UniqueFieldErrorCode.EMAIL)).when(registrationValidator).validateEmail(email);

        assertThrows(RegistrationException.class, ()-> authService.processRegistration(registrationRequest, mockMultipartFile, mockHttpServletResponse));

        verify(registrationValidator).validateEmail(email);
        verifyNoInteractions(userService, avatarService, verificationTokenService, refreshTokenService, jwtTokenService);
    }
}
