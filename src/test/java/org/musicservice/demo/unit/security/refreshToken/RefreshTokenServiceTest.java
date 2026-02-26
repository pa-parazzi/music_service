package org.musicservice.demo.unit.security.refreshToken;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.musicservice.demo.entity.auth.RefreshToken;
import org.musicservice.demo.exception.VerifyRefreshTokenException;
import org.musicservice.demo.security.cookie.CookieManager;
import org.musicservice.demo.security.cookie.CookieService;
import org.musicservice.demo.security.properties.RefreshTokenProperties;
import org.musicservice.demo.security.refreshToken.RefreshTokenCryptoService;
import org.musicservice.demo.security.refreshToken.RefreshTokenService;
import org.musicservice.demo.security.refreshToken.RefreshTokenRepository;
import org.musicservice.demo.support.factory.unit.auth.RefreshTokenFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private RefreshTokenCryptoService refreshTokenCryptoService;
    @Mock
    private CookieService cookieService;
    @Mock
    private CookieManager cookieManager;
    @Mock
    private RefreshTokenProperties refreshTokenProperties;

    @Mock
    private MockHttpServletRequest request;
    @Mock
    private MockHttpServletResponse response;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    void verifyRequest_ShouldAccessVerifyRequestAndReturnFoundRefreshTokenByHash_WhenRequestNotNull(){
        RefreshToken refreshToken = RefreshTokenFactory.validRefreshToken();
        String refreshTokenValue = RefreshTokenFactory.refreshTokenValue();
        String hash = refreshToken.getTokenHash();
        when(cookieService.getRefreshTokenByCookie(request)).thenReturn(refreshTokenValue);
        when(refreshTokenCryptoService.hash(refreshTokenValue)).thenReturn(hash);
        when(refreshTokenRepository.findByTokenHash(hash)).thenReturn(Optional.of(refreshToken));

        RefreshToken result = refreshTokenService.verifyRequest(request);

        assertEquals(refreshToken, result);

        verify(cookieService).getRefreshTokenByCookie(request);
        verify(refreshTokenCryptoService).hash(refreshTokenValue);
        verify(refreshTokenRepository).findByTokenHash(hash);
        verifyNoMoreInteractions(refreshTokenRepository);
    }

    @Test
    void verifyRequest_ShouldThrowVerifyRefreshTokenException_WhenRefreshTokenValueIsNull(){
        when(cookieService.getRefreshTokenByCookie(request)).thenReturn(null);
        assertThrows(VerifyRefreshTokenException.class, ()-> refreshTokenService.verifyRequest(request));
        verifyNoInteractions(refreshTokenRepository, refreshTokenCryptoService);
    }

    @Test
    void rotation_ShouldRotateRefreshToken_WhenExpired(){
        RefreshToken refreshToken = RefreshTokenFactory.expiredRefreshToken();
        String newTokenValue = RefreshTokenFactory.refreshTokenValue();
        String newHash = RefreshTokenFactory.newRefreshTokenHash();
        Duration duration = RefreshTokenFactory.duration();
        when(refreshTokenCryptoService.generateRefreshToken()).thenReturn(newTokenValue);
        when(refreshTokenCryptoService.hash(newTokenValue)).thenReturn(newHash);
        when(refreshTokenProperties.getDuration()).thenReturn(duration);

        refreshTokenService.rotation(refreshToken, response);

        ArgumentCaptor<Instant> expiryDateCaptor = ArgumentCaptor.forClass(Instant.class);
        verify(refreshTokenRepository).rotation(eq(refreshToken.getUserId()), eq(newHash), expiryDateCaptor.capture());
        Instant expiryDate = expiryDateCaptor.getValue();
        assertTrue(expiryDate.isAfter(Instant.now().plus(duration).minusSeconds(1)));

        assertFalse(Boolean.parseBoolean(refreshToken.getTokenHash()), newHash);

        verify(cookieManager).setCookie(response, newTokenValue);
    }

    @Test
    void rotation_ShouldNothing_WhenRefreshTokenIsNotExpired(){
        RefreshToken refreshToken = RefreshTokenFactory.validRefreshToken();

        refreshTokenService.rotation(refreshToken, response);

        verifyNoInteractions(refreshTokenCryptoService, refreshTokenRepository, cookieManager);
    }

    @Test
    void dropToken_ShouldDeleteRefreshToken_WhenCookieValueIsPresent(){
        RefreshToken refreshToken = RefreshTokenFactory.validRefreshToken();
        String refreshTokenValue = RefreshTokenFactory.refreshTokenValue();
        String hash = refreshToken.getTokenHash();
        when(cookieService.getRefreshTokenByCookie(request)).thenReturn(refreshTokenValue);
        when(refreshTokenCryptoService.hash(refreshTokenValue)).thenReturn(hash);
        when(refreshTokenRepository.findByTokenHash(hash)).thenReturn(Optional.of(refreshToken));

        refreshTokenService.dropToken(request, response);

        verify(refreshTokenRepository).delete(refreshToken);
        verify(cookieManager).clearCookie(response);
        verifyNoMoreInteractions(refreshTokenRepository);
    }

    @Test
    void dropToken_ShouldNothing_WhenCookieValueIsNull(){
        when(cookieService.getRefreshTokenByCookie(request)).thenReturn(null);

        refreshTokenService.dropToken(request, response);

        verify(cookieManager).clearCookie(response);
        verifyNoMoreInteractions(cookieManager);
        verifyNoInteractions(refreshTokenCryptoService, refreshTokenRepository);
    }

    @Test
    void create_ShouldCreateValidRefreshToken(){
        String tokenValue = RefreshTokenFactory.refreshTokenValue();
        String hash = RefreshTokenFactory.newRefreshTokenHash();
        Long userId = RefreshTokenFactory.userId();
        Duration duration = RefreshTokenFactory.duration();

        when(refreshTokenCryptoService.generateRefreshToken()).thenReturn(tokenValue);
        when(refreshTokenCryptoService.hash(tokenValue)).thenReturn(hash);
        when(refreshTokenProperties.getDuration()).thenReturn(duration);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken createdToken = refreshTokenService.create(userId, response);

        assertEquals(userId, createdToken.getUserId());
        assertTrue(createdToken.getExpiryDate().isAfter(Instant.now().plus(duration).minusSeconds(1)));
        assertEquals(hash, createdToken.getTokenHash());
        assertFalse(createdToken.getRevoked());
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }


}
