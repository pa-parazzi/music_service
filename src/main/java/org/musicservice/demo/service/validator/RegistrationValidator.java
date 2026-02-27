package org.musicservice.demo.service.validator;

import org.musicservice.demo.exception.user.RegistrationException;
import org.musicservice.demo.error.user.UniqueFieldErrorCode;
import org.musicservice.demo.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RegistrationValidator {

    private final UserRepository userRepository;

    @Autowired
    public RegistrationValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateUsername(String username){
        if(userRepository.existsByUsername(username)){
            throw new RegistrationException("Пользователь с таким именем уже существует", UniqueFieldErrorCode.USERNAME);
        }
    }

    public void validateEmail(String email){
        if(userRepository.existsByEmail(email)){
            throw new RegistrationException("Пользователь с таким email уже существует", UniqueFieldErrorCode.EMAIL);
        }
    }
}
