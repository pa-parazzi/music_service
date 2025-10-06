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

    // TODO: кастыль, переделать исключение
    public RefreshToken searchByTokenHash(String hash){
        return refreshTokenRepository.findByTokenHash(hash).orElseThrow(()-> new RuntimeException("refresh-token не найден в базе данных"));
    }

    // Генерация нового jwt-token, старый refresh-token помечается как revoked
    // Создание нового refresh-token'а, добавление его в заголовок SET_COOKIE
    // Возвращаем новый jwt-token
    @Transactional
    public String generateAccessByRefreshToken(HttpServletRequest request, HttpServletResponse response){

        String refreshTokenByCookie = CookieUtil.getRefreshTokenByCookie(request);
        System.out.println("Берем токен из cookie: " + refreshTokenByCookie);

        RefreshToken foundToken = searchByTokenHash(TokenUtil.hash(refreshTokenByCookie));
        System.out.println("Ищем токен в БД " + foundToken);

        System.out.println("Находим пользоваля по refresh_token.user_id из БД ");
        User user = userService.searchById(foundToken.getUser().getId());

        System.out.println("Генерируем новый jwt-token для пользователя");
        String accessToken = jwtUtil.generateToken(user.getUsername());

        System.out.println("Старый refreshToken помечается revoked");
        revoke(foundToken);

        System.out.println("Создаю новый refreshToken, перед этим почистив cookie от старых записей ");
        setResponseCookieAndAddHeader(request, response, user.getUsername());
        return accessToken;
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
    public RefreshToken createNewRefreshTokenByUsername(String username){

        String token = TokenUtil.generateRefreshToken();
        String hash = TokenUtil.hash(token);
        RefreshToken refreshToken = new RefreshToken();

        Optional<User> optionalUser = userService.getUserOptional(username);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            refreshToken.setUser(user);
            user.setRefreshToken(refreshToken);
        }
        refreshToken.setTokenHash(hash);
        refreshToken.setRevoked(false);
        refreshToken.setExpiryDate(LocalDateTime.now().plus(duration));
        refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    // Создает новый refresh-token по имени пользователя + задает его в заголовок ответа cookie с соответствующими настройками
    @Transactional
    public void setResponseCookieAndAddHeader(HttpServletRequest request, HttpServletResponse response, String username){
        // Чистим cookie перед запросом
        ResponseCookie clearCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, clearCookie.toString());

        // TODO: вернись сюда, здесь ошибка срабатывает catch 
        User user = userService.searchByUsername(username);
        try {
            RefreshToken refreshTokenByUsername = refreshTokenRepository.findByUserId(user.getId());
            System.out.println("refreshToken - " + refreshTokenByUsername);
            revoke(refreshTokenByUsername);
            refreshTokenRepository.delete(refreshTokenByUsername);
            user.setRefreshToken(null);
        } catch (RuntimeException e){
            throw new RuntimeException("Ошибка удаления refreshToken");
        }

    }

    @Transactional
    public void revoke(RefreshToken token){
        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }
}
