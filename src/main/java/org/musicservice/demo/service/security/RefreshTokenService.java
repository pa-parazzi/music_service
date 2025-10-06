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
import java.io.IOException;
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

    // Валидация/проверка на null refresh-token'а пришедшего из запроса (request)
    // Генерация нового jwt-token, старый refresh-token помечается как revoked
    // Создание нового refresh-token'а, добавление его в заголовок SET_COOKIE
    // Возвращаем новый jwt-token
    @Transactional
    public Map<String, String> generateAccessByRefreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String refreshTokenByCookie = CookieUtil.getRefreshTokenByCookie(request);
        if(refreshTokenByCookie==null){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return Map.of("error", "cookie is not valid");
        }
        Optional<RefreshToken> tokenOptional = refreshTokenRepository.findByTokenHash(TokenUtil.hash(refreshTokenByCookie));
        if(tokenOptional.isEmpty()){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return Map.of("error", "refresh-token is not should be empty");
        }
        RefreshToken refreshToken = tokenOptional.get();
        if(refreshToken.getRevoked() || refreshToken.getExpiryDate().isBefore(LocalDateTime.now())){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return Map.of("error", "refresh-token is not valid");
        }
        User user = userService.searchById(refreshToken.getUser().getId());
        String accessToken = jwtUtil.generateToken(user.getUsername());

        revoke(refreshToken);

        setResponseCookieAndAddHeader(response, user.getUsername());
        return Map.of("jwt-token", accessToken);
    }

    @Transactional
    public void clearRefreshTokenCookie(HttpServletRequest request, HttpServletResponse response){
        String refreshToken = CookieUtil.getRefreshTokenByCookie(request);
        Optional<RefreshToken> opt = refreshTokenRepository.findByTokenHash(TokenUtil.hash(refreshToken));
        if(opt.isEmpty()){
            return;
        }
        RefreshToken foundToken = opt.get();
        revoke(foundToken);
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    // Создание нового refresh-token по username
    // Возвращает refresh-token в виде строки String
    @Transactional
    public String createNewRefreshTokenByUsername(String username){
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

    // Создает новый refresh-token по имени пользователя + задает его в заголовок ответа cookie с соответствующими настройками
    @Transactional
    public void setResponseCookieAndAddHeader(HttpServletResponse response, String username){
        String refreshToken = createNewRefreshTokenByUsername(username);
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofDays(30))
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    @Transactional
    public void revoke(RefreshToken token){
        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }
}
