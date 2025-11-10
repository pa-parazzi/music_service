package org.musicservice.demo.service.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.musicservice.demo.Authority.Authority;
import org.musicservice.demo.configuration.YandexCloud.YandexStorageProperties;
import org.musicservice.demo.configuration.security.LoginSecurityProperties;
import org.musicservice.demo.dto.image.AvatarDto;
import org.musicservice.demo.dto.user.UserDtoForRegistration;
import org.musicservice.demo.dto.user.UserDtoForView;
import org.musicservice.demo.mapper.user.AvatarMapper;
import org.musicservice.demo.mapper.user.UserMapper;
import org.musicservice.demo.model.user.User;
import org.musicservice.demo.model.user.VerificationToken;
import org.musicservice.demo.repository.user.UserRepository;
import org.musicservice.demo.security.jwtAuthentication.cookie.CookieUtil;
import org.musicservice.demo.security.jwtAuthentication.jwt.JWTUtil;
import org.musicservice.demo.service.image.UserAvatarService;
import org.musicservice.demo.service.s3.S3UrlGenerator;
import org.musicservice.demo.service.security.JwtTokenService;
import org.musicservice.demo.service.security.RefreshTokenService;
import org.musicservice.demo.service.security.UserDetailsServiceImpl;
import org.musicservice.demo.service.security.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserMapper userMapper;
    private final UserAvatarService userAvatarService;
    private final RefreshTokenService refreshTokenService;
    private final VerificationTokenService verificationTokenService;
    private final JwtTokenService jwtTokenService;

    @Autowired
    public UserService(UserRepository userRepository, AuthService authService, UserDetailsServiceImpl userDetailsService, UserMapper userMapper, UserAvatarService userAvatarService, RefreshTokenService refreshTokenService, VerificationTokenService verificationTokenService, JwtTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.userDetailsService = userDetailsService;
        this.userMapper = userMapper;
        this.userAvatarService = userAvatarService;
        this.refreshTokenService = refreshTokenService;
        this.verificationTokenService = verificationTokenService;
        this.jwtTokenService = jwtTokenService;
    }

    public User searchByUsername(String username){
        return userRepository.searchByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }

    public User searchById(Long userId){
        return userRepository.findById(userId).orElseThrow(()-> new UsernameNotFoundException("Пользователь не найден"));
    }

    public Optional<User> getUserOptionalByUsername(String username){
        return userRepository.searchByUsername(username);
    }

    @Transactional
    public UserDtoForView viewSingle(String username){
        User user = searchByUsername(username);
        AvatarDto avatarDto = userAvatarService.getAvatarByUser(user);
        UserDtoForView userDto = userMapper.getUserDtoForView(user);
        userDto.setAvatar(avatarDto);
        return userDto;
    }

    @Transactional
    public String processRegistrationUser(UserDtoForRegistration userForRegistration, MultipartFile file, HttpServletResponse response){
        User regUser = userMapper.convertFromUserDtoForRegistrationToUser(userForRegistration);
        User user = authService.registration(regUser);
        verificationTokenService.createToken(user);
        refreshTokenService.createRefreshToken(response, user);
        userAvatarService.createOrGet(file, user);
        return jwtTokenService.generateAccess(user.getUsername());
    }

    @Transactional
    public String processLogin(HttpServletRequest request , HttpServletResponse response, String username){
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return authService.getJwtByCookieOrGet(userDetails, response, request);
    }

    @Transactional
    public void processFailedLogin(User user){
        authService.failLogin(user);
    }

    @Transactional
    public void resetFailedLogin(User user){
        user.setFailedLoginAttempts(0);
        user.setLockTime(null);
        userRepository.save(user);
    }

}
