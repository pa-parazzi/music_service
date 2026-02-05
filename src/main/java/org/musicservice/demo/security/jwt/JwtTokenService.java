package org.musicservice.demo.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.musicservice.demo.security.dto.TokenSubject;
import org.musicservice.demo.security.properties.JwtTokenProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;


@Component
public class JwtTokenService {

    private final JWTVerifier jwtVerifier;
    private final JwtTokenProperties jwtTokenProperties;
    private final Algorithm algorithm;

    @Autowired
    public JwtTokenService(JwtTokenProperties jwtTokenProperties) {
        this.jwtTokenProperties = jwtTokenProperties;
        this.algorithm = Algorithm.HMAC256(jwtTokenProperties.getJwtSecretKey());
        this.jwtVerifier = JWT.require(algorithm)
                .withIssuer(jwtTokenProperties.getIssuer())
                .acceptLeeway(jwtTokenProperties.getLeewaySeconds())
                .build();
    }

    public String generateToken(TokenSubject subject){
        Instant now = Instant.now();
        Date expirationDate = Date.from(now.plus(jwtTokenProperties.getAccessTokenDuration()));
        return JWT.create()
                .withIssuer(jwtTokenProperties.getIssuer())
                .withSubject(subject.userId().toString()) // если userId = null -> NPE
                .withArrayClaim("roles", subject.roles().toArray(String[]::new))
                .withIssuedAt(Date.from(now))
                .withExpiresAt(expirationDate)
                .withJWTId(UUID.randomUUID().toString())
                .sign(algorithm);
    }

    public DecodedJWT validateToken(String token) throws JWTVerificationException {
        return jwtVerifier.verify(token);
    }

}
