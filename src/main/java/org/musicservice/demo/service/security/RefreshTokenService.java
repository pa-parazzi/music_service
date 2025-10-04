package org.musicservice.demo.service.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.musicservice.demo.cookie.CookieUtil;
import org.musicservice.demo.model.user.RefreshToken;
import org.musicservice.demo.model.user.User;
import org.musicservice.demo.repository.user.RefreshTokenRepository;
import org.musicservice.demo.security.token.JWTUtil;
import org.musicservice.demo.security.token.TokenUtil;
import org.musicservice.demo.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.sasl.AuthenticationException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;
    private final Duration duration = Duration.ofDays(30);
    private final JWTUtil jwtUtil;

    @Autowired
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserService userService, JWTUtil jwtUtil) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    public Map<String, String> generateAccessByRefreshToken(HttpServletRequest request, HttpServletResponse response){
        String refreshTokenByCookie = CookieUtil.getRefreshTokenByCookie(request);
        if(refreshTokenByCookie==null){
            return Map.of("401 status code", "refresh-token is null");
        }
        Optional<RefreshToken> tokenOptional = refreshTokenRepository.findByTokenHash(TokenUtil.hash(refreshTokenByCookie));
        if(tokenOptional.isEmpty()){
            return Map.of("401 status code", "refresh-token is not present");
        }
        RefreshToken refreshToken = tokenOptional.get();
        if(refreshToken.getRevoked() || refreshToken.getExpiryDate().isBefore(LocalDateTime.now())){
            return Map.of("401 status code", "refresh-token is not valid");
        }
        User user = userService.searchById(refreshToken.getUser().getId());
        String accessToken = jwtUtil.generateToken(user.getUsername());

        String newRefreshToken = createAndPersist(user.getUsername());
        revoke(refreshToken);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", newRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofDays(30))
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return Map.of("jwt-token", accessToken);
    }

    @Transactional
    public String createAndPersist(String username){
        String token = TokenUtil.generateRefreshToken();
        String hash = TokenUtil.hash(token);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setTokenHash(hash);
        refreshToken.setUser(userService.searchByUsername(username));
        refreshToken.setRevoked(false);
        refreshToken.setExpiryDate(LocalDateTime.now().plus(duration));
        refreshTokenRepository.save(refreshToken);
        return token;
    }

    @Transactional
    public void revoke(RefreshToken token){
        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }
}
