package org.musicservice.demo.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.musicservice.demo.security.dto.TokenSubject;
import org.musicservice.demo.security.properties.AuthenticationTokenProperties;
import org.musicservice.demo.security.userDetails.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;


@Component
public class JwtTokenService {

    private final JWTVerifier jwtVerifier;
    private final AuthenticationTokenProperties properties;
    private final Algorithm algorithm;

    @Autowired
    public JwtTokenService(@Value("${jwt_secret_key}") String secret,
                           AuthenticationTokenProperties properties) {
        this.properties = properties;
        this.algorithm = Algorithm.HMAC256(secret);
        this.jwtVerifier = JWT.require(algorithm)
                .withIssuer("app-name")
                .acceptLeeway(30)
                .build();
    }

    public String generateToken(TokenSubject subject){
        Instant now = Instant.now();
        Date expirationDate = Date.from(now.plus(properties.getAccessTokenDuration()));
        return JWT.create()
                .withIssuer("app-name")
                .withSubject(subject.userId().toString())
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
