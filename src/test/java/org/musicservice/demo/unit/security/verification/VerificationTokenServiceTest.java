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
import org.musicservice.demo.security.verification.MailService;
import org.musicservice.demo.security.verification.VerificationTokenService;
import org.musicservice.demo.support.factory.unit.auth.VerificationTokenFactory;
import org.musicservice.demo.support.factory.unit.user.UserDataFactory;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VerificationTokenServiceTest {

    @Mock
    private VerificationTokenRepository verificationTokenRepository;
    @Mock
    private VerificationTokenProperties properties;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MailService mailService;

    @InjectMocks
    private VerificationTokenService verificationTokenService;

    @Test
    void createToken_ShouldCreateValidToken(){
        User user = UserDataFactory.user();
        VerifyEmailRequest verifyEmailRequest = VerificationTokenFactory.verifyEmailRequest();
        Duration duration = Duration.ofHours(24);
        String activationLink = "http://activation/";
        when(properties.getExpirationHours()).thenReturn(duration);
        when(userRepository.getReferenceById(verifyEmailRequest.userId())).thenReturn(user);
        when(properties.getActivationUrl()).thenReturn(activationLink);

        verificationTokenService.createToken(verifyEmailRequest);

        ArgumentCaptor<VerificationToken> verificationTokenArgumentCaptor = ArgumentCaptor.forClass(VerificationToken.class);
        verify(verificationTokenRepository).save(verificationTokenArgumentCaptor.capture());
        VerificationToken verificationToken = verificationTokenArgumentCaptor.getValue();
        assertEquals(verificationToken.getUser(), user);
        assertTrue(verificationToken.getExpiryDate().isAfter(Instant.now().plus(duration).minusSeconds(1)));

        ArgumentCaptor<String> emailCapture = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> activationLinkCapture = ArgumentCaptor.forClass(String.class);
        verify(mailService).sendActivationEmail(emailCapture.capture(), activationLinkCapture.capture());
        String actualEmail = emailCapture.getValue();
        String actualLink = activationLinkCapture.getValue();
        assertEquals(actualEmail, verifyEmailRequest.email());
        assertTrue(actualLink.startsWith(activationLink));
    }

}
