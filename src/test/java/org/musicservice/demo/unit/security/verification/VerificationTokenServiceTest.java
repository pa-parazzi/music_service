package org.musicservice.demo.unit.security.verification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.musicservice.demo.entity.auth.VerificationToken;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.repository.user.UserRepository;
import org.musicservice.demo.security.dto.VerifyEmailRequest;
import org.musicservice.demo.security.properties.VerificationTokenProperties;
import org.musicservice.demo.security.reposiroty.VerificationTokenRepository;
import org.musicservice.demo.security.verification.EmailService;
import org.musicservice.demo.security.verification.VerificationTokenService;
import org.musicservice.demo.support.factory.auth.AuthenticationDataFactory;
import org.musicservice.demo.support.factory.user.ValidUserDataFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VerificationTokenServiceTest {

    @Mock
    private VerificationTokenRepository verificationTokenRepository;
    @Mock
    private VerificationTokenProperties properties;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private VerificationTokenService verificationTokenService;

    @Test
    void createToken_ShouldCreateValidToken(){
        VerifyEmailRequest request = AuthenticationDataFactory.verifyEmailRequest();
        User user = ValidUserDataFactory.user();
        String activateLink = "http://activation_url/";

        when(properties.getExpirationHours()).thenReturn(Duration.ofHours(24));
        when(userRepository.getReferenceById(request.userId())).thenReturn(user);
        when(properties.getActivationUrl()).thenReturn(activateLink);

        verificationTokenService.createToken(request);

        ArgumentCaptor<VerificationToken> captor = ArgumentCaptor.forClass(VerificationToken.class);
        verify(verificationTokenRepository).save(captor.capture());
        VerificationToken savedToken = captor.getValue();

        assertEquals(user, savedToken.getUser());
        assertNotNull(savedToken.getToken());
        assertTrue(savedToken.getExpiryDate().isAfter(Instant.now()));

        ArgumentCaptor<String> linkCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailService).sendActivationEmail(eq(request.email()), linkCaptor.capture());

        String link = linkCaptor.getValue();
        assertTrue(link.startsWith(activateLink));
        assertTrue(link.contains(savedToken.getToken()));

    }



}
