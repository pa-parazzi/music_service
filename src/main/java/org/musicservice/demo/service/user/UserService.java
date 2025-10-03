package org.musicservice.demo.service.user;

import org.musicservice.demo.Authority.Authority;
import org.musicservice.demo.configuration.YandexCloud.YandexStorageProperties;
import org.musicservice.demo.configuration.security.LoginSecurityProperties;
import org.musicservice.demo.dto.admin.AdminDto;
import org.musicservice.demo.dto.image.AvatarDto;
import org.musicservice.demo.dto.user.UserDtoForRegistration;
import org.musicservice.demo.dto.user.UserDtoForView;
import org.musicservice.demo.mapper.AdminMapper;
import org.musicservice.demo.mapper.AvatarMapper;
import org.musicservice.demo.mapper.UserMapper;
import org.musicservice.demo.model.image.Avatar;
import org.musicservice.demo.model.user.User;
import org.musicservice.demo.repository.user.UserRepository;
import org.musicservice.demo.service.image.AvatarService;
import org.musicservice.demo.service.image.S3ImgUrlGenerator;
import org.musicservice.demo.service.security.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final PasswordEncoder passwordEncoder;
    private final LoginSecurityProperties securityProperties;
    private final VerificationTokenService verificationTokenService;
    private final UserMapper userMapper;
    private final AdminMapper adminMapper;
    private final AvatarService avatarService;
    private final AvatarMapper avatarMapper;
    private final S3ImgUrlGenerator s3ImgUrlGenerator;
    private final YandexStorageProperties yandexStorageProperties;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, LoginSecurityProperties securityProperties, VerificationTokenService verificationTokenService, UserMapper userMapper, AdminMapper adminMapper, AvatarService avatarService, AvatarMapper avatarMapper, S3ImgUrlGenerator s3ImgUrlGenerator, YandexStorageProperties yandexStorageProperties) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.securityProperties = securityProperties;
        this.verificationTokenService = verificationTokenService;
        this.userMapper = userMapper;
        this.adminMapper = adminMapper;
        this.avatarService = avatarService;
        this.avatarMapper = avatarMapper;
        this.s3ImgUrlGenerator = s3ImgUrlGenerator;
        this.yandexStorageProperties = yandexStorageProperties;
    }

    public AdminDto viewInfoAdmin(String username){
        User user = searchByUsername(username);
        return adminMapper.convertToAdmin(user);
    }

    public UserDtoForView viewSingle(String username){
        User user = searchByUsername(username);
        AvatarDto avatarDto = avatarMapper.convertToDto(user.getAvatar());
        String url = s3ImgUrlGenerator.generatePresignedUrl(yandexStorageProperties.getBuckets().get("img"), user.getAvatar().getKey());
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

    public Optional<User> getUserOptional(String username){
        return userRepository.searchByUsername(username);
    }

    @Transactional
    public void registrationUser(UserDtoForRegistration userForRegistration, MultipartFile avatar){
        User user = userMapper.convertFromUserDtoForRegistrationToUser(userForRegistration);
        user.setRole(Authority.USER);
        user.setEnabled(false);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        avatarService.create(avatar, user);
        verificationTokenService.createToken(user);
    }

    @Transactional
    public void registrationUserWithAvatarDefault(UserDtoForRegistration userForRegistration){
        User user = userMapper.convertFromUserDtoForRegistrationToUser(userForRegistration);
        user.setRole(Authority.USER);
        user.setEnabled(false);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        avatarService.createDefaultAvatar(user);
        verificationTokenService.createToken(user);
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

//    @Transactional
//    public void createAdmin(AdminDto adminDto){
//        User user = adminMapper.convertToUser(adminDto);
//        user.setEmail("igor.bocharov.88@gmail.com");
//        user.setDateOfBirth(LocalDate.of(2003, 05, 11));
//        user.setRole(Authority.ADMIN);
//        user.setEnabled(true);
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        userRepository.save(user);
//    }

}
