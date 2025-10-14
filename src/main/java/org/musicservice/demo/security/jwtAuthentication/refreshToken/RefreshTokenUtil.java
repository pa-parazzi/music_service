package org.musicservice.demo.security.jwtAuthentication.refreshToken;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class RefreshTokenUtil {

    private final PasswordEncoder passwordEncoder;
    private static final SecureRandom secureRandom = new SecureRandom();

    public RefreshTokenUtil() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public static String generateRefreshToken(){
        byte [] bytes = new byte[48];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public String hash(String token){
        return passwordEncoder.encode(token);
    }

    public boolean matches(String rawToken, String hash){
        return passwordEncoder.matches(rawToken, hash);
    }

//    public static String hash(String token){
//        try{
//            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
//            byte [] digest = messageDigest.digest(token.getBytes(StandardCharsets.UTF_8));
//            StringBuilder builder = new StringBuilder();
//            for(byte b: digest){
//                builder.append(String.format("%02x", b));
//            }
//            return builder.toString();
//        } catch (Exception e){
//            throw new RuntimeException(e);
//        }
//    }
}
