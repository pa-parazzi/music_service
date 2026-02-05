package org.musicservice.demo.unit.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.musicservice.demo.Authority.Authority;
import org.musicservice.demo.security.dto.TokenSubject;
import org.musicservice.demo.security.jwt.JwtTokenService;
import org.musicservice.demo.security.properties.JwtTokenProperties;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtTokenServiceTest {

    private JwtTokenProperties jwtTokenProperties;

    private JwtTokenService jwtTokenService;

    @BeforeEach
    void setUp(){
        jwtTokenProperties = mock(JwtTokenProperties.class);
        when(jwtTokenProperties.getJwtSecretKey()).thenReturn("secret");
        when(jwtTokenProperties.getIssuer()).thenReturn("app-name");
        when(jwtTokenProperties.getLeewaySeconds()).thenReturn(30L);
        jwtTokenService = new JwtTokenService(jwtTokenProperties);
    }

    @Test
    void generateToken_ShouldContainCorrectClaims(){
        TokenSubject subject = new TokenSubject(1L, List.of(Authority.USER.getAuthority()));
        when(jwtTokenProperties.getAccessTokenDuration()).thenReturn(Duration.ofMinutes(15));

        String jwtToken = jwtTokenService.generateToken(subject);
        DecodedJWT decodedJWT = JWT.decode(jwtToken);

        assertEquals(jwtTokenProperties.getIssuer(), decodedJWT.getIssuer());
        assertEquals(subject.userId().toString(), decodedJWT.getSubject());
        assertEquals(subject.roles(), decodedJWT.getClaim("roles").asList(String.class));
        assertNotNull(decodedJWT.getId());
        assertNotNull(decodedJWT.getIssuedAt());
        assertNotNull(decodedJWT.getExpiresAt());
    }

    @Test
    void validateToken_shouldReturnDecodedJWT_whenTokenValid(){
        TokenSubject subject = new TokenSubject(1L, List.of(Authority.USER.getAuthority()));
        when(jwtTokenProperties.getAccessTokenDuration()).thenReturn(Duration.ofMinutes(15));

        String jwtTokenValue = jwtTokenService.generateToken(subject);
        DecodedJWT decodedJWT = jwtTokenService.validateToken(jwtTokenValue);

        assertEquals(subject.userId().toString(), decodedJWT.getSubject());
    }

    @Test
    void validateToken_shouldThrow_whenTokenInvalid(){
        String invalidToken = "invalid";

        assertThrows(JWTVerificationException.class, ()-> jwtTokenService.validateToken(invalidToken));
    }

}
