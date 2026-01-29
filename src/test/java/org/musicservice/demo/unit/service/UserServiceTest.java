package org.musicservice.demo.unit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.musicservice.demo.Authority.Authority;
import org.musicservice.demo.dto.user.RegistrationRequest;
import org.musicservice.demo.dto.user.UserMainResponse;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.exception.UserNotFoundException;
import org.musicservice.demo.mapper.user.UserMapper;
import org.musicservice.demo.repository.user.UserRepository;
import org.musicservice.demo.service.user.UserService;
import org.musicservice.demo.support.factory.ValidUserDataFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


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
        final RegistrationRequest registrationRequest = ValidUserDataFactory.registrationRequest();
        final String encodingPassword = "encoded";

        when(passwordEncoder.encode(registrationRequest.getPassword())).thenReturn(encodingPassword);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0)); // вернули тот же объект, что был передан в save()

        final User result = userService.create(registrationRequest);

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
        final User expectedUser = ValidUserDataFactory.userWithAvatar();
        final Long userId = expectedUser.getId();

        when(userRepository.searchByIdWithAvatar(userId)).thenReturn(Optional.of(expectedUser));

        final User result = userService.searchByIdWithAvatar(userId);

        assertEquals(expectedUser.getId(), result.getId());
        assertEquals(expectedUser.getUsername(), result.getUsername());
        assertNotNull(result.getUserAvatar());

        verify(userRepository).searchByIdWithAvatar(userId);
    }

    @Test
    void viewMainResponseByIdTest_ReturnValidResponse(){
        final User expectedUser = ValidUserDataFactory.userWithAvatar();
        final Long userId = expectedUser.getId();
        final UserMainResponse expectedResponse = ValidUserDataFactory.userMainResponse(expectedUser);

        when(userRepository.searchByIdWithAvatar(userId)).thenReturn(Optional.of(expectedUser));
        when(userMapper.toMainResponse(expectedUser)).thenReturn(expectedResponse);

        final UserMainResponse result = userService.viewMainResponseById(userId);

        assertEquals(userId, result.getId());
        assertEquals(expectedResponse.getUsername(), result.getUsername());
        assertNotNull(result.getAvatar());

        verify(userRepository).searchByIdWithAvatar(userId);
        verify(userMapper).toMainResponse(expectedUser);
    }

    @Test
    void searchByUsernameWithAvatarTest_ReturnValidUser(){
        final User expectedUser = ValidUserDataFactory.userWithAvatar();
        final String username = expectedUser.getUsername();

        when(userRepository.searchByUsernameWithAvatar(username)).thenReturn(Optional.of(expectedUser));

        final User result = userService.searchByUsernameWithAvatar(username);

        assertEquals(expectedUser.getId(), result.getId());
        assertEquals(username, result.getUsername());
        assertNotNull(result.getUserAvatar());

        verify(userRepository).searchByUsernameWithAvatar(username);
    }


    /** Negatives cases */


    @Test
    void searchByIdWithAvatarTest_ThrowUserNotFoundException(){
        final Long userId = 1L;

        when(userRepository.searchByIdWithAvatar(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, ()-> userService.searchByIdWithAvatar(userId));

        verify(userRepository).searchByIdWithAvatar(userId);
    }

    @Test
    void searchByUsernameWithAvatar_ThrowUserNotFoundException(){
        final String username = "name";

        when(userRepository.searchByUsernameWithAvatar(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, ()-> userService.searchByUsernameWithAvatar(username));

        verify(userRepository).searchByUsernameWithAvatar(username);
    }



}
