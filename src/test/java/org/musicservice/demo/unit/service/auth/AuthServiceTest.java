package org.musicservice.demo.unit.service.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import org.musicservice.demo.support.factory.auth.JwtTokenFactory;
import org.musicservice.demo.support.factory.auth.RefreshTokenFactory;
import org.musicservice.demo.support.factory.user.ValidUserDataFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
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
        User user = ValidUserDataFactory.user();
        String jwtValue = JwtTokenFactory.value();
        when(userService.create(registrationRequest)).thenReturn(user);
        when(jwtTokenService.generateToken(any(TokenSubject.class))).thenReturn(jwtValue);

        TokenResponse result = authService.processRegistration(registrationRequest, mockMultipartFile, mockHttpServletResponse);

        assertEquals(jwtValue, result.accessToken());

        verify(registrationValidator).validateUsername(registrationRequest.getUsername());
        verify(registrationValidator).validateEmail(registrationRequest.getEmail());
        verify(userService).create(registrationRequest);
        verify(avatarService).createOrGetDefault(mockMultipartFile, user);

        ArgumentCaptor<VerifyEmailRequest> verifyEmailRequestCaptor = ArgumentCaptor.forClass(VerifyEmailRequest.class);
        verify(verificationTokenService).createToken(verifyEmailRequestCaptor.capture());
        VerifyEmailRequest verifyEmailRequest = verifyEmailRequestCaptor.getValue();
        assertEquals(user.getId(), verifyEmailRequest.userId());
        assertEquals(user.getEmail(), verifyEmailRequest.email());

        ArgumentCaptor<TokenSubject> tokenSubjectCaptor = ArgumentCaptor.forClass(TokenSubject.class);
        verify(jwtTokenService).generateToken(tokenSubjectCaptor.capture());
        TokenSubject tokenSubject = tokenSubjectCaptor.getValue();
        assertEquals(user.getId(), tokenSubject.userId());
        assertTrue(tokenSubject.roles().contains(user.getRole().getAuthority()));
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
        Collection<String> roles = principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        Authentication authentication = AuthenticationDataFactory.authentication(principal);
        String jwtValue = JwtTokenFactory.value();

        when(jwtTokenService.generateToken(any(TokenSubject.class))).thenReturn(jwtValue);

        TokenResponse result = authService.processLogin(authentication, mockHttpServletResponse);

        assertEquals(jwtValue, result.accessToken());

        InOrder inOrder = inOrder(refreshTokenService);
        inOrder.verify(refreshTokenService).deleteByUserId(userId, mockHttpServletResponse);
        inOrder.verify(refreshTokenService).create(userId, mockHttpServletResponse);

        ArgumentCaptor<TokenSubject> tokenSubjectCaptor = ArgumentCaptor.forClass(TokenSubject.class);
        verify(jwtTokenService).generateToken(tokenSubjectCaptor.capture());
        TokenSubject tokenSubject = tokenSubjectCaptor.getValue();
        assertEquals(userId, tokenSubject.userId());
        assertEquals(roles, tokenSubject.roles());

        verifyNoMoreInteractions(refreshTokenService, jwtTokenService);
    }

    @Test
    void refreshAccess_ShouldRotateRefreshTokenAndIssueNewAccessToken(){
        UserPrincipal principal = AuthenticationDataFactory.principal();
        Long userId = principal.userId();
        Collection<String> roles = principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        RefreshToken refreshToken = RefreshTokenFactory.validRefreshToken();
        String jwtValue = JwtTokenFactory.value();

        when(refreshTokenService.verifyRequest(mockHttpServletRequest)).thenReturn(refreshToken);
        when(userDetailsService.loadPrincipalById(userId)).thenReturn(principal);
        when(jwtTokenService.generateToken(any(TokenSubject.class))).thenReturn(jwtValue);

        TokenResponse result = authService.refreshAccess(mockHttpServletResponse, mockHttpServletRequest);

        assertEquals(jwtValue, result.accessToken());

        InOrder inOrder = inOrder(refreshTokenService, userDetailsService, jwtTokenService);

        inOrder.verify(refreshTokenService).verifyRequest(mockHttpServletRequest);
        inOrder.verify(refreshTokenService).rotation(refreshToken, mockHttpServletResponse);
        inOrder.verify(userDetailsService).loadPrincipalById(userId);

        ArgumentCaptor<TokenSubject> tokenSubjectCaptor = ArgumentCaptor.forClass(TokenSubject.class);
        inOrder.verify(jwtTokenService).generateToken(tokenSubjectCaptor.capture());
        TokenSubject tokenSubject = tokenSubjectCaptor.getValue();
        assertEquals(userId, tokenSubject.userId());
        assertEquals(roles, tokenSubject.roles());

        verifyNoMoreInteractions(refreshTokenService, userDetailsService, jwtTokenService);
    }

}
