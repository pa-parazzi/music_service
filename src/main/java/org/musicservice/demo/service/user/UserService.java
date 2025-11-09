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
import org.musicservice.demo.model.image.UserAvatar;
import org.musicservice.demo.model.user.User;
import org.musicservice.demo.repository.user.UserRepository;
import org.musicservice.demo.security.jwtAuthentication.cookie.CookieUtil;
import org.musicservice.demo.security.jwtAuthentication.jwt.JWTUtil;
import org.musicservice.demo.service.image.UserAvatarService;
import org.musicservice.demo.service.s3.S3UrlGenerator;
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
    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final LoginSecurityProperties securityProperties;
    private final VerificationTokenService verificationTokenService;
    private final UserMapper userMapper;
    private final UserAvatarService userAvatarService;
    private final AvatarMapper avatarMapper;
    private final S3UrlGenerator s3UrlGenerator;
    private final YandexStorageProperties yandexStorageProperties;
    private final RefreshTokenService refreshTokenService;
    private final JWTUtil jwtUtil;

    @Autowired
    public UserService(UserRepository userRepository, UserDetailsServiceImpl userDetailsService, PasswordEncoder passwordEncoder, LoginSecurityProperties securityProperties, VerificationTokenService verificationTokenService, UserMapper userMapper, UserAvatarService userAvatarService, AvatarMapper avatarMapper, S3UrlGenerator s3UrlGenerator, YandexStorageProperties yandexStorageProperties, RefreshTokenService refreshTokenService, JWTUtil jwtUtil) {
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.securityProperties = securityProperties;
        this.verificationTokenService = verificationTokenService;
        this.userMapper = userMapper;
        this.userAvatarService = userAvatarService;
        this.avatarMapper = avatarMapper;
        this.s3UrlGenerator = s3UrlGenerator;
        this.yandexStorageProperties = yandexStorageProperties;
        this.refreshTokenService = refreshTokenService;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public UserDtoForView viewSingle(String username){
        User user = searchByUsername(username);
        AvatarDto avatarDto = avatarMapper.convertToDto(user.getUserAvatar());
        String url = s3UrlGenerator.generatePublicUrl(yandexStorageProperties.getBuckets().get("img"), user.getUserAvatar().getKey());
        avatarDto.setUrl(url);
        UserDtoForView userDto = userMapper.getUserDtoForView(user);
        userDto.setAvatar(avatarDto);
        return userDto;
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
    public User registrationUser(UserDtoForRegistration userForRegistration, MultipartFile file){
        Authority authority = Authority.USER;
        String password = passwordEncoder.encode(userForRegistration.getPassword());
        User newUser = new User(userForRegistration.getUsername(), password, userForRegistration.getEmail(), userForRegistration.getDateOfBirth(), false, authority);
        userRepository.save(newUser);
        userAvatarService.createOrGet(file, newUser);
        verificationTokenService.createToken(newUser);
        return newUser;
    }

    @Transactional
    public String processLogin(HttpServletRequest request , HttpServletResponse response, String username){
        User user = searchByUsername(username);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String refreshTokenByCookie = CookieUtil.getRefreshTokenByCookie(request);
        if(refreshTokenByCookie==null && user.getRefreshToken()==null){
            refreshTokenService.createRefreshToken(response, user);
            return jwtUtil.generateToken(userDetails);
        }
        return jwtUtil.generateToken(userDetails);
    }

    @Transactional
    public void processFailedLogin(User user){
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
