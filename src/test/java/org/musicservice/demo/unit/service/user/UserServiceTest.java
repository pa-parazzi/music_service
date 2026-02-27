package org.musicservice.demo.unit.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.musicservice.demo.entity.user.Authority;
import org.musicservice.demo.dto.user.RegistrationRequest;
import org.musicservice.demo.dto.user.UserMainResponse;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.exception.user.UserNotFoundException;
import org.musicservice.demo.mapper.user.UserMapper;
import org.musicservice.demo.repository.user.UserRepository;
import org.musicservice.demo.service.user.UserService;
import org.musicservice.demo.support.factory.unit.user.UserDataFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    /** Positives Cases*/

    @Test
    void createUserTest_ReturnValidUser(){
        RegistrationRequest registrationRequest = UserDataFactory.registrationRequest();
        String encodingPassword = "encoded";

        when(passwordEncoder.encode(registrationRequest.getPassword())).thenReturn(encodingPassword);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0)); // вернули тот же объект, что был передан в save()

        User result = userService.create(registrationRequest);

        assertEquals(registrationRequest.getUsername(), result.getUsername());
        assertEquals(encodingPassword, result.getPassword());
        assertEquals(registrationRequest.getEmail(), result.getEmail());
        assertEquals(registrationRequest.getDateOfBirth(), result.getDateOfBirth());
        assertEquals(Authority.USER, result.getRole());

        verify(passwordEncoder).encode(registrationRequest.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void searchByIdWithAvatarTest_ReturnValidUser(){
        User expectedUser = UserDataFactory.userWithAvatar();
        Long userId = expectedUser.getId();

        when(userRepository.findByIdWithAvatar(userId)).thenReturn(Optional.of(expectedUser));

        User result = userService.searchByIdWithAvatar(userId);

        assertEquals(expectedUser.getId(), result.getId());
        assertEquals(expectedUser.getUsername(), result.getUsername());
        assertNotNull(result.getUserAvatar());

        verify(userRepository).findByIdWithAvatar(userId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void viewMainResponseByIdTest_ReturnValidResponse(){
        User expectedUser = UserDataFactory.userWithAvatar();
        Long userId = expectedUser.getId();
        UserMainResponse expectedResponse = UserDataFactory.userMainResponse(expectedUser);

        when(userRepository.findByIdWithAvatar(userId)).thenReturn(Optional.of(expectedUser));
        when(userMapper.toMainResponse(expectedUser)).thenReturn(expectedResponse);

        UserMainResponse result = userService.viewMainResponseById(userId);

        assertEquals(userId, result.getId());
        assertEquals(expectedResponse.getUsername(), result.getUsername());
        assertNotNull(result.getAvatar());

        verify(userRepository).findByIdWithAvatar(userId);
        verify(userMapper).toMainResponse(expectedUser);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void searchByUsernameWithAvatarTest_ReturnValidUser(){
        User expectedUser = UserDataFactory.userWithAvatar();
        String username = expectedUser.getUsername();

        when(userRepository.findByUsernameWithAvatar(username)).thenReturn(Optional.of(expectedUser));

        User result = userService.searchByUsernameWithAvatar(username);

        assertEquals(expectedUser.getId(), result.getId());
        assertEquals(username, result.getUsername());
        assertNotNull(result.getUserAvatar());

        verify(userRepository).findByUsernameWithAvatar(username);
        verifyNoMoreInteractions(userRepository);
    }


    /** Negatives cases */

    @Test
    void searchByIdWithAvatarTest_ThrowUserNotFoundException(){
        Long userId = 1L;

        when(userRepository.findByIdWithAvatar(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, ()-> userService.searchByIdWithAvatar(userId));

        verify(userRepository).findByIdWithAvatar(userId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void searchByUsernameWithAvatar_ThrowUserNotFoundException(){
        String username = "name";

        when(userRepository.findByUsernameWithAvatar(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, ()-> userService.searchByUsernameWithAvatar(username));

        verify(userRepository).findByUsernameWithAvatar(username);
        verifyNoMoreInteractions(userRepository);
    }

}
