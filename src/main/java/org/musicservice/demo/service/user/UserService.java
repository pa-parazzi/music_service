package org.musicservice.demo.service.user;

import org.musicservice.demo.Authority.Authority;
import org.musicservice.demo.dto.user.RegistrationRequest;
import org.musicservice.demo.dto.user.UserMainResponse;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.mapper.user.UserMapper;
import org.musicservice.demo.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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

    public User searchByUsername(String username){
        return userRepository.searchByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }

    public User searchByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("Пользователь с email: " + email + "не найден"));
    }

    public User searchById(Long userId){
        return userRepository.findById(userId).orElseThrow(()-> new UsernameNotFoundException("Пользователь не найден"));
    }

    public Optional<User> findOptByUsername(String username){
        return userRepository.searchByUsername(username);
    }

    @Transactional
    public User create(RegistrationRequest regRequest){
        String password = passwordEncoder.encode(regRequest.getPassword());
        User user = new User(regRequest.getUsername(), password, regRequest.getEmail(), regRequest.getDateOfBirth(), Authority.USER);
        return userRepository.save(user);
    }

    public UserMainResponse viewSingle(String username){
        return userMapper.toMainResponse(searchByUsername(username));
    }

    @Transactional
    public void cleanAll(){
        userRepository.deleteAll();
    }

}
