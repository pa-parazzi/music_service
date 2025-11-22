package org.musicservice.demo.service.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.musicservice.demo.dto.image.AvatarDto;
import org.musicservice.demo.dto.user.UserDtoForRegistration;
import org.musicservice.demo.dto.user.UserDtoForView;
import org.musicservice.demo.mapper.user.UserMapper;
import org.musicservice.demo.model.user.User;
import org.musicservice.demo.repository.user.UserRepository;
import org.musicservice.demo.service.image.UserAvatarService;
import org.musicservice.demo.service.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserMapper userMapper;
    private final UserAvatarService userAvatarService;

    @Autowired
    public UserService(UserRepository userRepository, AuthService authService, UserDetailsServiceImpl userDetailsService, UserMapper userMapper, UserAvatarService userAvatarService) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.userDetailsService = userDetailsService;
        this.userMapper = userMapper;
        this.userAvatarService = userAvatarService;
    }

    public User searchByUsername(String username){
        return userRepository.searchByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }

    public User searchByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("Пользователь с email: " + email + "не найден"));
    }

    public User searchById(Long userId){
        return userRepository.findById(userId).orElseThrow(()-> new UsernameNotFoundException("Пользователь не найден"));
    }

    public Optional<User> getUserOptionalByUsername(String username){
        return userRepository.searchByUsername(username);
    }

    @Transactional
    public void deleteAll(){
        userRepository.deleteAll();
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
        User user = userMapper.convertFromUserDtoForRegistrationToUser(userForRegistration);
        User regUser = authService.registration(response, user);
        userAvatarService.createOrGet(file, regUser);
        return authService.generateJwt(regUser.getUsername());
    }

    @Transactional
    public String processLogin(HttpServletRequest request , HttpServletResponse response, String username){
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return authService.generateJwtOrGet(userDetails, response, request);
    }

}
