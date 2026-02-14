package org.musicservice.demo.unit.security.verification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class VerificationTokenServiceTest {

    @Test
    void createToken_ShouldCreateValidToken(){
//        VerifyEmailRequest request = VerificationEmailFactory.verifyEmailRequest();
//        User user = ValidUserDataFactory.user();
//        String activateLink = "http://activation_url/";
//
//        when(properties.getExpirationHours()).thenReturn(Duration.ofHours(24));
//        when(userRepository.getReferenceById(request.userId())).thenReturn(user);
//        when(properties.getActivationUrl()).thenReturn(activateLink);
//
//        verificationTokenService.createToken(request);
//
//        ArgumentCaptor<VerificationToken> captor = ArgumentCaptor.forClass(VerificationToken.class);
//        verify(verificationTokenRepository).save(captor.capture());
//        VerificationToken savedToken = captor.getValue();
//
//        assertEquals(user, savedToken.getUser());
//        assertNotNull(savedToken.getToken());
//        assertTrue(savedToken.getExpiryDate().isAfter(Instant.now()));
//
//        ArgumentCaptor<String> linkCaptor = ArgumentCaptor.forClass(String.class);
//        verify(smtpMailService).sendActivationEmail(eq(request.email()), linkCaptor.capture());
//
//        String link = linkCaptor.getValue();
//        assertTrue(link.startsWith(activateLink));
//        assertTrue(link.contains(savedToken.getToken()));

    }



}
