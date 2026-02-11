package org.musicservice.demo.integration.controller.auth;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.musicservice.demo.dto.user.RegistrationRequest;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.repository.user.UserRepository;
import org.musicservice.demo.security.cookie.CookieProperties;
import org.musicservice.demo.security.dto.TokenResponse;
import org.musicservice.demo.service.yandexCloud.properties.YandexStorageProperties;
import org.musicservice.demo.support.config.AbstractIntegrationTest;
import org.musicservice.demo.support.factory.user.ValidUserDataFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class AuthRestControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CookieProperties cookieProperties;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private YandexStorageProperties yandexStorageProperties;

    @Autowired
    private ObjectMapper objectMapper;

//    @BeforeEach
//    void cleanData(){
//        userRepository.deleteAll();
//    }

    @Test
    @Transactional
    void shouldRegisterUserWithDefaultAvatarAndReturnAccessToken_WhenMultipartFileFromUserAvatarIsNull() throws Exception{
        // given
        RegistrationRequest registrationRequest = ValidUserDataFactory.registrationRequest();
        String userJson = objectMapper.writeValueAsString(registrationRequest);
        String defaultAvatarKey = yandexStorageProperties.getDefaultAvatarKey();

        MockMultipartFile userPart = userPartSetUp(userJson);

        // when
        MvcResult result = mockMvc.perform(multipart("/api/auth/registration")
                .file(userPart)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();


        // then
        String jsonResult = result.getResponse().getContentAsString();
        TokenResponse response = objectMapper.readValue(jsonResult, TokenResponse.class);

        assertThat(response.accessToken()).isNotBlank();

        Cookie cookieResponse = result.getResponse().getCookie(cookieProperties.getRefreshTokenName());

        assertThat(cookieResponse).isNotNull();
        assertThat(cookieResponse.getValue()).isNotBlank();
        assertThat(cookieResponse.isHttpOnly()).isEqualTo(cookieProperties.getHttpOnly());
        assertThat(cookieResponse.getSecure()).isEqualTo(cookieProperties.getSecure());
        assertThat(cookieResponse.getPath()).isEqualTo(cookieProperties.getPath());

        User user = userRepository.findByUsername(registrationRequest.getUsername()).orElseThrow();

        assertThat(registrationRequest.getUsername()).isEqualTo(user.getUsername());
        assertThat(defaultAvatarKey).isEqualTo(user.getUserAvatar().getKey());
        assertThat(passwordEncoder.matches(registrationRequest.getPassword(), user.getPassword())).isTrue();
        assertThat(registrationRequest.getEmail()).isEqualTo(user.getEmail());
        assertThat(registrationRequest.getDateOfBirth()).isEqualTo(user.getDateOfBirth());
        assertThat(user.isEnabled()).isFalse();
        
    }

    @Test
    @Transactional
    void shouldRegisterUserWithNewAvatarAndReturnAccessToken_WhenMultipartFileFromUserAvatarIsPresent() throws Exception{
        RegistrationRequest request = ValidUserDataFactory.registrationRequest();
        String userJson = objectMapper.writeValueAsString(request);

        MockMultipartFile userPart = userPartSetUp(userJson);

        MockMultipartFile avatarPart = new MockMultipartFile(
                "file",
                "new_avatar.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "new_avatar".getBytes());

        MvcResult result = mockMvc.perform(multipart("/api/auth/registration")
                .file(userPart)
                .file(avatarPart)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        TokenResponse response = objectMapper.readValue(jsonResult, TokenResponse.class);

        assertThat(response.accessToken()).isNotBlank();

        Cookie cookie = result.getResponse().getCookie(cookieProperties.getRefreshTokenName());
        assertThat(cookie).isNotNull();
        assertThat(cookie.getValue()).isNotBlank();
        assertThat(cookie.isHttpOnly()).isEqualTo(cookieProperties.getHttpOnly());
        assertThat(cookie.getSecure()).isEqualTo(cookieProperties.getSecure());
        assertThat(cookie.getPath()).isEqualTo(cookieProperties.getPath());

        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();

        assertThat(yandexStorageProperties.getDefaultAvatarKey()).isNotEqualTo(user.getUserAvatar().getKey());
        assertThat(request.getUsername()).isEqualTo(user.getUsername());
        assertThat(request.getEmail()).isEqualTo(user.getEmail());
        assertThat(request.getDateOfBirth()).isEqualTo(user.getDateOfBirth());
        assertThat(passwordEncoder.matches(request.getPassword(), user.getPassword())).isTrue();
        assertThat(user.isEnabled()).isFalse();
    }




    private MockMultipartFile userPartSetUp(String json){
        return new MockMultipartFile(
                "user",
                "user.json",
                MediaType.APPLICATION_JSON_VALUE,
                json.getBytes(StandardCharsets.UTF_8));
    }


}
