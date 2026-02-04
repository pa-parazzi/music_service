package org.musicservice.demo.unit.service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.musicservice.demo.exception.RegistrationException;
import org.musicservice.demo.exception.response.UniqueFieldErrorCode;
import org.musicservice.demo.repository.user.UserRepository;
import org.musicservice.demo.service.validator.RegistrationValidator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RegistrationValidatorTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RegistrationValidator registrationValidator;

    @Test
    void validateUsername_ShouldThrowException_WhenUsernameAlreadyExists(){
        String username = "Alex";
        when(userRepository.existsByUsername(username)).thenReturn(true);
        RegistrationException exception = assertThrows(RegistrationException.class, ()-> registrationValidator.validateUsername(username));
        assertEquals(UniqueFieldErrorCode.USERNAME, exception.getCode());
    }

    @Test
    void validateEmail_ShouldThrowException_WhenEmailAlreadyExists(){
        String email = "alex@gmail.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);
        RegistrationException exception = assertThrows(RegistrationException.class, ()-> registrationValidator.validateEmail(email));
        assertEquals(UniqueFieldErrorCode.EMAIL, exception.getCode());
    }

    @Test
    void validateUsername_ShouldNotThrow_WhenUsernameIsUnique(){
        String username = "Alex";
        when(userRepository.existsByUsername(username)).thenReturn(false);
        assertDoesNotThrow(()-> registrationValidator.validateUsername(username));
    }

    @Test
    void validateEmail_ShouldNotThrow_WhenEmailIsUnique(){
        String email = "alex@gmail.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);
        assertDoesNotThrow(()-> registrationValidator.validateEmail(email));
    }

}
