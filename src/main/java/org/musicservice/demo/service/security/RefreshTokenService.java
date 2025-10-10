package org.musicservice.demo.service.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.musicservice.demo.configuration.security.RefreshTokenProperties;
import org.musicservice.demo.dto.user.UserDtoForLogin;
import org.musicservice.demo.exception.refreshTokenError.RefreshTokenNotFoundException;
import org.musicservice.demo.security.jwtAuthentication.cookie.CookieManager;
import org.musicservice.demo.security.jwtAuthentication.cookie.CookieUtil;
import org.musicservice.demo.model.user.RefreshToken;
import org.musicservice.demo.model.user.User;
import org.musicservice.demo.repository.user.RefreshTokenRepository;
import org.musicservice.demo.security.jwtAuthentication.jwt.JWTUtil;
import org.musicservice.demo.security.jwtAuthentication.refreshToken.RefreshTokenUtil;
import org.musicservice.demo.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final CookieManager cookieManager;
    private final RefreshTokenProperties refreshTokenProperties;
    private final JWTUtil jwtUtil;

    @Autowired
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, CookieManager cookieManager, RefreshTokenProperties refreshTokenProperties, JWTUtil jwtUtil) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.cookieManager = cookieManager;
        this.refreshTokenProperties = refreshTokenProperties;
        this.jwtUtil = jwtUtil;
    }

    public RefreshToken searchByTokenHash(String hash){
        return refreshTokenRepository.findByTokenHash(hash).orElseThrow(()-> new RefreshTokenNotFoundException("refreshToken is not a found"));
    }

    public Optional<RefreshToken> getOptTokenByHash(String hash){
        return refreshTokenRepository.findByTokenHash(hash);
    }

    // Создает новый refreshToken для пользователя по его username, заранее задает токен в cookie
    // если refreshToken был найден в Cookie и он совпал с тем что есть в БД, тогда из метода возвращется старый токен
    // Метод для логина в контроллере
    @Transactional
    public RefreshToken createOrGetCurrent(HttpServletRequest request, User user){
        // Берем токен из cookie, если в куки есть токен - ищем в бд и выдаем старый
        String refreshTokenByCookie = CookieUtil.getRefreshTokenByCookie(request);
        if(refreshTokenByCookie!=null){
            Optional<RefreshToken> foundToken = refreshTokenRepository.findByTokenHash(RefreshTokenUtil.hash(refreshTokenByCookie));
            if(foundToken.isPresent()){
                return foundToken.get();
            }
        }
        String refreshTokenByGenerate = RefreshTokenUtil.generateRefreshToken();
        String hash = RefreshTokenUtil.hash(refreshTokenByGenerate);

        RefreshToken newRefreshToken = new RefreshToken();
        newRefreshToken.setExpiryDate(LocalDateTime.now().plus(refreshTokenProperties.getDuration()));
        newRefreshToken.setRevoked(false);
        newRefreshToken.setTokenHash(hash);
        newRefreshToken.setUser(user);
        user.setRefreshToken(newRefreshToken);
        return newRefreshToken;
    }


    @Transactional
    public String generateJwtFromLogin(RefreshToken refreshToken){
        RefreshToken foundToken = searchByTokenHash(refreshToken.getTokenHash());
        User user = foundToken.getUser();
        return jwtUtil.generateToken(user.getUsername());
    }

    @Transactional
    public String refreshJwtToken(HttpServletRequest request){
        String refreshTokenByCookie = CookieUtil.getRefreshTokenByCookie(request);
        if(refreshTokenByCookie==null){
            return null;
        }
        RefreshToken foundToken = searchByTokenHash(RefreshTokenUtil.hash(refreshTokenByCookie));
        User user = foundToken.getUser();
        return jwtUtil.generateToken(user.getUsername());
    }

    @Transactional
    public RefreshToken save(RefreshToken refreshToken){
        return refreshTokenRepository.save(refreshToken);
    }

    // Удаление токена, отвязка от связанной сущности User
    @Transactional
    public void delete(HttpServletRequest request){
        String refreshTokenByCookie = CookieUtil.getRefreshTokenByCookie(request);
        if(refreshTokenByCookie!=null){
            RefreshToken foundToken = searchByTokenHash(RefreshTokenUtil.hash(refreshTokenByCookie));
            foundToken.getUser().setRefreshToken(null);
            refreshTokenRepository.delete(foundToken);
        }
    }

}
