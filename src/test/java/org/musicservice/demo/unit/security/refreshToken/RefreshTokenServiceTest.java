package org.musicservice.demo.unit.security.refreshToken;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.musicservice.demo.security.reposiroty.RefreshTokenRepository;
import org.musicservice.demo.support.factory.auth.AuthenticationDataFactory;
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
        RefreshToken refreshToken = AuthenticationDataFactory.validRefreshToken();
        String refreshTokenValue = AuthenticationDataFactory.refreshTokenValue();
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
    void verifyRequest_ShouldThrowVerifyRefreshTokenException_WhenRequestIsNull(){
        when(cookieService.getRefreshTokenByCookie(request)).thenReturn(null);
        assertThrows(VerifyRefreshTokenException.class, ()-> refreshTokenService.verifyRequest(request));
        verifyNoInteractions(refreshTokenRepository, refreshTokenCryptoService);
    }

    @Test
    void rotation_ShouldRotateRefreshToken_WhenExpired(){
        RefreshToken refreshToken = AuthenticationDataFactory.expiredRefreshToken();
        String newTokenValue = AuthenticationDataFactory.refreshTokenValue();
        String hash = "new-token-hash";
        when(refreshTokenCryptoService.generateRefreshToken()).thenReturn(newTokenValue);
        when(refreshTokenCryptoService.hash(newTokenValue)).thenReturn(hash);
        when(refreshTokenProperties.getDuration()).thenReturn(Duration.ofHours(24));

        refreshTokenService.rotation(refreshToken, response);

        verify(refreshTokenRepository).rotation(any(Long.class), eq(hash), any(Instant.class));
        verify(cookieManager).setCookie(response, newTokenValue);
    }

    @Test
    void rotation_ShouldNothing_WhenNotExpired(){
        RefreshToken refreshToken = AuthenticationDataFactory.validRefreshToken();

        refreshTokenService.rotation(refreshToken, response);

        verifyNoInteractions(refreshTokenCryptoService, refreshTokenRepository, cookieManager);
    }

    @Test
    void dropToken_ShouldDeleteRefreshToken_WhenCookieValueNotNull(){
        RefreshToken refreshToken = AuthenticationDataFactory.validRefreshToken();
        String refreshTokenValue = AuthenticationDataFactory.refreshTokenValue();
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

        verifyNoInteractions(refreshTokenCryptoService, refreshTokenRepository, cookieManager);
    }

    @Test
    void create_ShouldCreateValidRefreshToken(){
        RefreshToken refreshToken = AuthenticationDataFactory.validRefreshToken();
        String tokenValue = AuthenticationDataFactory.refreshTokenValue();
        String hash = refreshToken.getTokenHash();
        Long userId = refreshToken.getUserId();

        when(refreshTokenCryptoService.generateRefreshToken()).thenReturn(tokenValue);
        when(refreshTokenCryptoService.hash(tokenValue)).thenReturn(hash);
        when(refreshTokenProperties.getDuration()).thenReturn(Duration.ofHours(24));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        RefreshToken result = refreshTokenService.create(userId, response);

        assertNotNull(result);
        assertEquals(hash, result.getTokenHash());
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }


}
