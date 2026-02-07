package org.musicservice.demo.unit.service.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.musicservice.demo.dto.user.RegistrationRequest;
import org.musicservice.demo.entity.auth.RefreshToken;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.exception.RegistrationException;
import org.musicservice.demo.exception.response.UniqueFieldErrorCode;
import org.musicservice.demo.security.dto.TokenResponse;
import org.musicservice.demo.security.dto.TokenSubject;
import org.musicservice.demo.security.dto.VerifyEmailRequest;
import org.musicservice.demo.security.jwt.JwtTokenService;
import org.musicservice.demo.security.refreshToken.RefreshTokenService;
import org.musicservice.demo.security.userDetails.UserDetailsServiceImpl;
import org.musicservice.demo.security.userDetails.UserPrincipal;
import org.musicservice.demo.security.verification.VerificationTokenService;
import org.musicservice.demo.service.auth.AuthService;
import org.musicservice.demo.service.image.UserAvatarService;
import org.musicservice.demo.service.user.UserService;
import org.musicservice.demo.service.validator.RegistrationValidator;
import org.musicservice.demo.support.factory.auth.AuthenticationDataFactory;
import org.musicservice.demo.support.factory.user.ValidUserDataFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserService userService;
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
    private final MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();

    @InjectMocks
    private AuthService authService;

    @Test
    void processRegistration_ShouldCreateUserAndIssueTokensAndSendVerification(){
        RegistrationRequest registrationRequest = ValidUserDataFactory.registrationRequest();
        User newUser = ValidUserDataFactory.user();
        String accessToken = AuthenticationDataFactory.accessToken();
        when(userService.create(registrationRequest)).thenReturn(newUser);
        when(jwtTokenService.generateToken(any(TokenSubject.class))).thenReturn(accessToken);

        TokenResponse result = authService.processRegistration(registrationRequest, mockMultipartFile, mockHttpServletResponse);

        assertEquals(accessToken, result.accessToken());

        verify(registrationValidator).validateUsername(registrationRequest.getUsername());
        verify(registrationValidator).validateEmail(registrationRequest.getEmail());
        verify(userService).create(registrationRequest);
        verify(avatarService).createOrGetDefault(eq(mockMultipartFile), eq(newUser));
        verify(verificationTokenService).createToken(any(VerifyEmailRequest.class));
        verify(refreshTokenService).create(eq(newUser.getId()), eq(mockHttpServletResponse));
        verify(jwtTokenService).generateToken(any(TokenSubject.class));
        verifyNoMoreInteractions(registrationValidator, userService, avatarService, verificationTokenService, refreshTokenService, jwtTokenService);
    }


    @Test
    void processRegistration_ShouldThrowRegistrationException_UsernameAlreadyExists(){
        RegistrationRequest registrationRequest = ValidUserDataFactory.registrationRequest();
        String username = registrationRequest.getUsername();

        doThrow(new RegistrationException("Validation error", UniqueFieldErrorCode.USERNAME)).when(registrationValidator).validateUsername(username);

        assertThrows(RegistrationException.class, ()-> authService.processRegistration(registrationRequest, mockMultipartFile, mockHttpServletResponse));

        verify(registrationValidator).validateUsername(username);
        verifyNoInteractions(userService, avatarService, verificationTokenService, refreshTokenService, jwtTokenService);
    }


    @Test
    void processRegistration_ShouldThrowRegistrationException_EmailAlreadyExists(){
        RegistrationRequest registrationRequest = ValidUserDataFactory.registrationRequest();
        String email = registrationRequest.getEmail();

        doThrow(new RegistrationException("Validation error", UniqueFieldErrorCode.EMAIL)).when(registrationValidator).validateEmail(email);

        assertThrows(RegistrationException.class, ()-> authService.processRegistration(registrationRequest, mockMultipartFile, mockHttpServletResponse));

        verify(registrationValidator).validateEmail(email);
        verifyNoInteractions(userService, avatarService, verificationTokenService, refreshTokenService, jwtTokenService);
    }

    @Test
    void processLogin_ShouldGenerateJwtAndRotateRefreshToken(){
        UserPrincipal principal = AuthenticationDataFactory.principal();
        Long userId = principal.userId();
        Authentication authentication = AuthenticationDataFactory.authentication(principal);
        String accessToken = AuthenticationDataFactory.accessToken();
        when(jwtTokenService.generateToken(any(TokenSubject.class))).thenReturn(accessToken);

        TokenResponse result = authService.processLogin(authentication, mockHttpServletResponse);

        assertEquals(accessToken, result.accessToken());

        InOrder inOrder = inOrder(refreshTokenService);
        inOrder.verify(refreshTokenService).deleteByUserId(userId, mockHttpServletResponse);
        inOrder.verify(refreshTokenService).create(userId, mockHttpServletResponse);

        verify(jwtTokenService).generateToken(any(TokenSubject.class));
        verifyNoMoreInteractions(refreshTokenService, jwtTokenService);
    }

    @Test
    void refreshAccess_ShouldRotateRefreshTokenAndIssueNewAccessToken(){
        UserPrincipal principal = AuthenticationDataFactory.principal();
        Long userId = principal.userId();
        RefreshToken refreshToken = AuthenticationDataFactory.validRefreshToken();
        String accessToken = AuthenticationDataFactory.accessToken();

        when(refreshTokenService.verifyRequest(mockHttpServletRequest)).thenReturn(refreshToken);
        when(userDetailsService.loadPrincipalById(userId)).thenReturn(principal);
        when(jwtTokenService.generateToken(any(TokenSubject.class))).thenReturn(accessToken);

        TokenResponse result = authService.refreshAccess(mockHttpServletResponse, mockHttpServletRequest);

        assertEquals(accessToken, result.accessToken());

        InOrder inOrder = inOrder(refreshTokenService, userDetailsService, jwtTokenService);

        inOrder.verify(refreshTokenService).verifyRequest(mockHttpServletRequest);
        inOrder.verify(refreshTokenService).rotation(refreshToken, mockHttpServletResponse);
        inOrder.verify(userDetailsService).loadPrincipalById(userId);
        inOrder.verify(jwtTokenService).generateToken(any(TokenSubject.class));
        verifyNoMoreInteractions(refreshTokenService, userDetailsService, jwtTokenService);
    }

}
