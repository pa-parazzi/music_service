package org.musicservice.demo.unit.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.musicservice.demo.dto.image.ImageResponse;
import org.musicservice.demo.entity.image.UserAvatar;
import org.musicservice.demo.entity.user.Authority;
import org.musicservice.demo.dto.user.RegistrationRequest;
import org.musicservice.demo.dto.user.UserMainResponse;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.exception.user.UserNotFoundException;
import org.musicservice.demo.mapper.image.ImageMapper;
import org.musicservice.demo.repository.image.UserAvatarRepository;
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
    private UserAvatarRepository userAvatarRepository;
    @Mock
    private ImageMapper imageMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_ReturnValidUser(){
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
    void mainResponse_ReturnValidResponse(){
        User user = UserDataFactory.user();
        Long userId = user.getId();
        String username = user.getUsername();
        UserAvatar userAvatar = UserDataFactory.userAvatar(user);
        ImageResponse expectedAvatarResponse = UserDataFactory.avatarResponse();
        when(userRepository.getUsernameById(userId)).thenReturn(Optional.of(username));
        when(userAvatarRepository.findByUserId(userId)).thenReturn(userAvatar);
        when(imageMapper.toImageResponse(userAvatar)).thenReturn(expectedAvatarResponse);

        UserMainResponse result = userService.mainResponse(userId);
        assertEquals(username, result.getUsername());
        assertEquals(result.getAvatar().key(), expectedAvatarResponse.key());
        assertEquals(result.getAvatar().url(), expectedAvatarResponse.url());
    }

    @Test
    void mainResponse_ThrowUserNotFoundException_WhenUsernameIsEmpty(){
        when(userRepository.getUsernameById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.mainResponse(any(Long.class)));
    }

}
