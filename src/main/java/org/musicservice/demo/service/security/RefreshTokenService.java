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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    public Optional<RefreshToken> searchByTokenHash(String hash){
        return refreshTokenRepository.findByTokenHash(hash);
    }

    public String generateNewJwtToken(RefreshToken refreshToken){
        String username = refreshToken.getUser().getUsername();
        Optional<User> foundUser = userService.getUserOptional(username);
        if(foundUser.isEmpty()){
            throw new UsernameNotFoundException("пользователь не найден");
        }
        User user = foundUser.get();
        return jwtUtil.generateToken(user.getUsername());
    }

    // Создает новый refreshToken для пользователя по его username, заранее задает токен в cookie
    @Transactional
    public RefreshToken createRefreshTokenFromUser(HttpServletResponse response, String username){
        String refreshTokenByGenerate = TokenUtil.generateRefreshToken();
        setCookie(response, refreshTokenByGenerate);
        String hash = TokenUtil.hash(refreshTokenByGenerate);

        RefreshToken newRefreshToken = new RefreshToken();
        newRefreshToken.setTokenHash(hash);
        newRefreshToken.setRevoked(false);
        newRefreshToken.setExpiryDate(LocalDateTime.now().plus(duration));
        User user = userService.searchByUsername(username);
        newRefreshToken.setUser(user);
        user.setRefreshToken(newRefreshToken);
        return refreshTokenRepository.save(newRefreshToken);
    }

    @Transactional
    public String refreshJwtToken(HttpServletRequest request, HttpServletResponse response){
        String refreshTokenByCookie = CookieUtil.getRefreshTokenByCookie(request);
        if(refreshTokenByCookie==null){
            throw new RuntimeException("refreshToken не был найден в cookie");
        }
        Optional<RefreshToken> foundToken = searchByTokenHash(TokenUtil.hash(refreshTokenByCookie));
        if(foundToken.isEmpty()){
            throw new RuntimeException("refreshToken не найден в БД");
        }
        RefreshToken refreshToken = foundToken.get();
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        return generateNewJwtToken(refreshToken);
    }

    // Проверяем есть ли refreshToken в cookie, если нет - выдаем новый refreshToken и задаем его в cookie
    @Transactional
    public boolean checkCookie(HttpServletRequest request, HttpServletResponse response, String username){
        String refreshTokenByCookie = CookieUtil.getRefreshTokenByCookie(request);
        if(refreshTokenByCookie==null){
            User user = userService.searchByUsername(username);
            refreshTokenRepository.delete(user.getRefreshToken());
            user.setRefreshToken(null);
            createRefreshTokenFromUser(response, user.getUsername());
            return true;
        }
        return false;
    }

    // Удаление токена, отвязка от связанной сущности User
    @Transactional
    public void delete(HttpServletRequest request){
        String refreshTokenByCookie = CookieUtil.getRefreshTokenByCookie(request);
        if(refreshTokenByCookie!=null){
            Optional<RefreshToken> foundToken = searchByTokenHash(TokenUtil.hash(refreshTokenByCookie));
            if(foundToken.isPresent()){
                RefreshToken refreshToken = foundToken.get();
                refreshTokenRepository.delete(refreshToken);
                refreshToken.getUser().setRefreshToken(null);
                refreshTokenRepository.save(refreshToken);
            }
        }
    }

    // Задает refreshToken в cookie в браузере
    public void setCookie(HttpServletResponse response, String refreshToken){
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(duration)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    // Очистка cookie
    public void clearCookie(HttpServletResponse response){
        ResponseCookie clearCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, clearCookie.toString());
    }

}
