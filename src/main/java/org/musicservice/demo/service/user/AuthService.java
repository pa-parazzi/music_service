package org.musicservice.demo.service.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.musicservice.demo.Authority.Authority;
import org.musicservice.demo.configuration.security.LoginSecurityProperties;
import org.musicservice.demo.model.user.User;
import org.musicservice.demo.repository.user.UserRepository;
import org.musicservice.demo.security.jwtAuthentication.cookie.CookieUtil;
import org.musicservice.demo.security.jwtAuthentication.jwt.JWTUtil;
import org.musicservice.demo.service.security.JwtTokenService;
import org.musicservice.demo.service.security.RefreshTokenService;
import org.musicservice.demo.service.security.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginSecurityProperties securityProperties;
    private final RefreshTokenService refreshTokenService;
    private final VerificationTokenService verificationTokenService;
    private final JwtTokenService jwtTokenService;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, LoginSecurityProperties securityProperties, RefreshTokenService refreshTokenService, VerificationTokenService verificationTokenService, JwtTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.securityProperties = securityProperties;
        this.refreshTokenService = refreshTokenService;
        this.verificationTokenService = verificationTokenService;
        this.jwtTokenService = jwtTokenService;
    }

    @Transactional
    public User registration(HttpServletResponse response, User user){
        Authority authority = Authority.USER;
        String password = passwordEncoder.encode(user.getPassword());
        User newUser = new User(user.getUsername(), password, user.getEmail(), user.getDateOfBirth(), false, authority);
        userRepository.save(newUser);
        verificationTokenService.createToken(newUser);
        refreshTokenService.createRefreshToken(response, newUser);
        return newUser;
    }

    public String generateJwt(String username){
        return jwtTokenService.generateAccess(username);
    }

    @Transactional
    public String generateJwtOrGet(UserDetails userDetails, HttpServletResponse response, HttpServletRequest request){
        User user = userRepository.findByUsername(userDetails.getUsername());
        String refreshTokenByCookie = CookieUtil.getRefreshTokenByCookie(request);
        if(refreshTokenByCookie==null && user.getRefreshToken()==null){
            refreshTokenService.createRefreshToken(response, user);
            return jwtTokenService.generateAccess(userDetails);
        }
        return jwtTokenService.generateAccess(userDetails);
    }

    @Transactional
    public void failLogin(User user){
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
        if(user.getFailedLoginAttempts()>= securityProperties.getMaxFailedAttempts()){
            user.setLockTime(LocalDateTime.now().plusMinutes(securityProperties.getLockDurationMinutes()));
        }
        userRepository.save(user);
    }

    @Transactional
    public void resetFailedLogin(User user){
        user.setFailedLoginAttempts(0);
        user.setLockTime(null);
        userRepository.save(user);
    }
}
