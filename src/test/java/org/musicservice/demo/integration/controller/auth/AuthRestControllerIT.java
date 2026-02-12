package org.musicservice.demo.integration.controller.auth;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.musicservice.demo.dto.user.RegistrationRequest;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.exception.RegistrationException;
import org.musicservice.demo.exception.response.ApiErrorResponse;
import org.musicservice.demo.exception.response.ErrorType;
import org.musicservice.demo.exception.response.UniqueFieldErrorCode;
import org.musicservice.demo.repository.user.UserRepository;
import org.musicservice.demo.security.cookie.CookieProperties;
import org.musicservice.demo.security.dto.TokenResponse;
import org.musicservice.demo.service.yandexCloud.properties.YandexStorageProperties;
import org.musicservice.demo.support.config.AbstractIntegrationTest;
import org.musicservice.demo.support.factory.multipartFile.MultipartFileFactory;
import org.musicservice.demo.support.factory.user.ValidUserDataFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

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
    private JdbcTemplate jdbcTemplate;
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

    private final MediaType contentMultipartForm = MediaType.MULTIPART_FORM_DATA;

    @BeforeEach
    void cleanData(){
        jdbcTemplate.execute("TRUNCATE TABLE users, refresh_token, verification_token RESTART IDENTITY CASCADE");
    }

    @Test
    void shouldRegisterUserWithDefaultAvatarAndReturnAccessToken_WhenMultipartFileFromUserAvatarIsNull() throws Exception{
        RegistrationRequest registrationRequest = ValidUserDataFactory.registrationRequest();
        String userJson = objectMapper.writeValueAsString(registrationRequest);
        String defaultAvatarKey = yandexStorageProperties.getDefaultAvatarKey();

        MockMultipartFile userPart = MultipartFileFactory.userPart(userJson);

        MvcResult result = mockMvc.perform(multipart("/api/auth/registration")
                .file(userPart)
                .contentType(contentMultipartForm))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

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
    void shouldRegisterUserWithNewAvatarAndReturnAccessToken_WhenMultipartFileFromUserAvatarIsPresent() throws Exception{
        RegistrationRequest request = ValidUserDataFactory.registrationRequest();
        String userJson = objectMapper.writeValueAsString(request);

        MockMultipartFile userPart = MultipartFileFactory.userPart(userJson);
        MockMultipartFile avatarPart = MultipartFileFactory.imagePart();

        MvcResult result = mockMvc.perform(multipart("/api/auth/registration")
                .file(userPart)
                .file(avatarPart)
                .contentType(contentMultipartForm))
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

    @Test
    void shouldThrowRegistrationException_WhenUsernameAlreadyExists() throws Exception{
        RegistrationRequest request = ValidUserDataFactory.registrationRequest();
        User user = ValidUserDataFactory.userWithUsernameAlreadyExistsByRegistrationRequest(request);
        userRepository.save(user);

        String userJson = objectMapper.writeValueAsString(request);
        MockMultipartFile userPart = MultipartFileFactory.userPart(userJson);

        MvcResult result = mockMvc.perform(multipart("/api/auth/registration")
                .file(userPart)
                .contentType(contentMultipartForm))
                .andExpect(status().isBadRequest())
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        ApiErrorResponse errorResponse = objectMapper.readValue(jsonResult, ApiErrorResponse.class);

        assertThat(errorResponse.code()).isEqualTo(ErrorType.REGISTRATION_ERROR.name());
        assertThat(errorResponse.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.fieldsError().containsKey(UniqueFieldErrorCode.USERNAME.getField())).isTrue();
    }

    @Test
    void shouldThrowRegistrationException_WhenEmailAlreadyExists() throws Exception{
        RegistrationRequest request = ValidUserDataFactory.registrationRequest();
        User user = ValidUserDataFactory.userWithEmailAlreadyExistsByRegistrationRequest(request);
        userRepository.save(user);

        String userJson = objectMapper.writeValueAsString(request);
        MockMultipartFile userPart = MultipartFileFactory.userPart(userJson);

        MvcResult result = mockMvc.perform(multipart("/api/auth/registration")
                        .file(userPart)
                        .contentType(contentMultipartForm))
                .andExpect(status().isBadRequest())
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        ApiErrorResponse errorResponse = objectMapper.readValue(jsonResult, ApiErrorResponse.class);

        assertThat(errorResponse.code()).isEqualTo(ErrorType.REGISTRATION_ERROR.name());
        assertThat(errorResponse.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.fieldsError().containsKey(UniqueFieldErrorCode.EMAIL.getField())).isTrue();
    }
}
