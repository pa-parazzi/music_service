package org.musicservice.demo.unit.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.musicservice.demo.security.dto.TokenSubject;
import org.musicservice.demo.security.jwt.JwtTokenService;
import org.musicservice.demo.security.properties.JwtTokenProperties;
import org.musicservice.demo.support.factory.auth.JwtTokenFactory;
import org.musicservice.demo.support.factory.auth.TokenSubjectFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtTokenServiceTest {

    private JwtTokenProperties jwtTokenProperties;

    private JwtTokenService jwtTokenService;

    @BeforeEach
    void setupJwtProperties(){
        jwtTokenProperties = mock(JwtTokenProperties.class);
        when(jwtTokenProperties.getJwtSecretKey()).thenReturn(JwtTokenFactory.secret());
        when(jwtTokenProperties.getIssuer()).thenReturn(JwtTokenFactory.issuer());
        when(jwtTokenProperties.getLeewaySeconds()).thenReturn(JwtTokenFactory.leewaySeconds());
        jwtTokenService = new JwtTokenService(jwtTokenProperties);
    }

    @Test
    void generateToken_ShouldContainCorrectClaims(){
        TokenSubject subject = TokenSubjectFactory.tokenSubject();
        when(jwtTokenProperties.getAccessTokenDuration()).thenReturn(JwtTokenFactory.duration());

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
    void validateToken_ShouldReturnDecodedJWT_whenTokenValid(){
        TokenSubject subject = TokenSubjectFactory.tokenSubject();
        when(jwtTokenProperties.getAccessTokenDuration()).thenReturn(JwtTokenFactory.duration());

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
