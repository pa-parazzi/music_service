package org.musicservice.demo.security.jwtAuthentication.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.musicservice.demo.service.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;


@Component
public class JWTUtil {

    @Value("${jwt_secret_key}")
    private String secret;

    public String generateToken(UserDetails userDetails){
        Date expirationDate = Date.from(ZonedDateTime.now().plusMinutes(1).toInstant());
        return JWT.create()
                .withSubject("User details")
                .withClaim("username", userDetails.getUsername())
                .withIssuedAt(new Date())
                .withIssuer("app-name")
                .withExpiresAt(expirationDate)
                .withJWTId(UUID.randomUUID().toString())
                .sign(Algorithm.HMAC256(secret));
    }

    public String validateToken(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                .withSubject("User details")
                .withIssuer("app-name")
                .build();
        DecodedJWT jwt = verifier.verify(token);
        return jwt.getClaim("username").asString();
    }

}
