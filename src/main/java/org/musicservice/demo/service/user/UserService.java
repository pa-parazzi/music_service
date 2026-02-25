package org.musicservice.demo.service.user;

import org.musicservice.demo.dto.user.RegistrationRequest;
import org.musicservice.demo.dto.user.UserMainResponse;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.exception.UserNotFoundException;
import org.musicservice.demo.mapper.user.UserMapper;
import org.musicservice.demo.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User create(RegistrationRequest regRequest){
        String password = passwordEncoder.encode(regRequest.getPassword());
        User user = new User(regRequest.getUsername(), password, regRequest.getEmail(), regRequest.getDateOfBirth());
        return userRepository.save(user);
    }

    public UserMainResponse viewMainResponseById(Long id){
        return userMapper.toMainResponse(searchByIdWithAvatar(id));
    }

    public User searchByIdWithAvatar(Long id){
        return userRepository.findByIdWithAvatar(id).orElseThrow(() -> new UserNotFoundException("User with id: " + id + " not found"));
    }

    public User searchByUsernameWithAvatar(String username){
        return userRepository.findByUsernameWithAvatar(username).orElseThrow(() -> new UserNotFoundException("User with username: " + username + " not found"));
    }


}
