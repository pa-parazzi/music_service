package org.musicservice.demo.security.refreshToken;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class RefreshTokenCryptoService {

    private static final SecureRandom secureRandom = new SecureRandom();

    public String generateRefreshToken(){
        byte [] bytes = new byte[64];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public String hash(String token){
        try{
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte [] digest = messageDigest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for(byte b: digest){
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
