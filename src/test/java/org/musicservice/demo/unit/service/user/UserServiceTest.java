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
import org.musicservice.demo.mapper.image.UserAvatarMapper;
import org.musicservice.demo.repository.image.UserAvatarRepository;
import org.musicservice.demo.repository.user.UserRepository;
import org.musicservice.demo.security.userDetails.UserPrincipal;
import org.musicservice.demo.service.user.UserService;
import org.musicservice.demo.support.factory.unit.user.UserDataFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    private UserAvatarMapper userAvatarMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

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
    void mainResponseTest_ReturnValidResponse(){
        User user = UserDataFactory.user();
        UserPrincipal principal = UserDataFactory.principal();
        UserAvatar userAvatar = UserDataFactory.userAvatar(user);
        ImageResponse expectedAvatarResponse = UserDataFactory.avatarResponse();
        when(userAvatarRepository.findByUserId(principal.userId())).thenReturn(userAvatar);
        when(userAvatarMapper.convertToDto(any(UserAvatar.class))).thenReturn(expectedAvatarResponse);

        UserMainResponse result = userService.mainResponse(principal);
        assertEquals(principal.userId(), result.getId());
        assertEquals(principal.username(), result.getUsername());
        assertEquals(result.getAvatar().getKey(), expectedAvatarResponse.getKey());
        assertEquals(result.getAvatar().getUrl(), expectedAvatarResponse.getUrl());
    }

}
