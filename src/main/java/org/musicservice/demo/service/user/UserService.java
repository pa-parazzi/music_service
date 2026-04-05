package org.musicservice.demo.service.user;

import org.musicservice.demo.dto.user.RegistrationRequest;
import org.musicservice.demo.dto.user.UserMainResponse;
import org.musicservice.demo.entity.image.UserAvatar;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.exception.user.UserNotFoundException;
import org.musicservice.demo.mapper.image.ImageMapper;
import org.musicservice.demo.repository.image.UserAvatarRepository;
import org.musicservice.demo.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserAvatarRepository userAvatarRepository;
    private final ImageMapper imageMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, UserAvatarRepository userAvatarRepository, ImageMapper imageMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userAvatarRepository = userAvatarRepository;
        this.imageMapper = imageMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User create(RegistrationRequest regRequest){
        String password = passwordEncoder.encode(regRequest.getPassword());
        User user = new User(regRequest.getUsername(), password, regRequest.getEmail(), regRequest.getDateOfBirth());
        return userRepository.save(user);
    }

    public UserMainResponse mainResponse(Long userId){
        UserMainResponse userResponse = new UserMainResponse();
        userResponse.setUsername(getUsernameById(userId));
        UserAvatar userAvatar = userAvatarRepository.findByUserId(userId);
        userResponse.setAvatar(imageMapper.toImageResponse(userAvatar));
        return userResponse;
    }

    public String getUsernameById(Long userId){
        return userRepository.getUsernameById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
    }

}
