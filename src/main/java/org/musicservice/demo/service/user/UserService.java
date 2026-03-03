package org.musicservice.demo.service.user;

import org.musicservice.demo.dto.user.RegistrationRequest;
import org.musicservice.demo.dto.user.UserMainResponse;
import org.musicservice.demo.entity.image.UserAvatar;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.mapper.image.UserAvatarMapper;
import org.musicservice.demo.repository.image.UserAvatarRepository;
import org.musicservice.demo.repository.user.UserRepository;
import org.musicservice.demo.security.userDetails.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserAvatarRepository userAvatarRepository;
    private final UserAvatarMapper userAvatarMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, UserAvatarRepository userAvatarRepository, UserAvatarMapper userAvatarMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userAvatarRepository = userAvatarRepository;
        this.userAvatarMapper = userAvatarMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User create(RegistrationRequest regRequest){
        String password = passwordEncoder.encode(regRequest.getPassword());
        User user = new User(regRequest.getUsername(), password, regRequest.getEmail(), regRequest.getDateOfBirth());
        return userRepository.save(user);
    }

    public UserMainResponse mainResponse(UserPrincipal principal){
        UserMainResponse userResponse = new UserMainResponse();
        userResponse.setId(principal.userId());
        userResponse.setUsername(principal.username());
        UserAvatar userAvatar = userAvatarRepository.findByUserId(principal.userId());
        userResponse.setAvatar(userAvatarMapper.convertToDto(userAvatar));
        return userResponse;
    }

}
