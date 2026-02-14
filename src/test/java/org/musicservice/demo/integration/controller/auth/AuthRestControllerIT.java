package org.musicservice.demo.integration.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.musicservice.demo.dto.user.LoginRequest;
import org.musicservice.demo.dto.user.RegistrationRequest;
import org.musicservice.demo.entity.auth.RefreshToken;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.exception.response.ApiErrorResponse;
import org.musicservice.demo.exception.response.AuthErrorCode;
import org.musicservice.demo.exception.response.ErrorType;
import org.musicservice.demo.exception.response.UniqueFieldErrorCode;
import org.musicservice.demo.repository.user.UserRepository;
import org.musicservice.demo.security.cookie.CookieProperties;
import org.musicservice.demo.security.dto.TokenResponse;
import org.musicservice.demo.security.properties.RefreshTokenProperties;
import org.musicservice.demo.security.properties.VerificationTokenProperties;
import org.musicservice.demo.security.refreshToken.RefreshTokenCryptoService;
import org.musicservice.demo.security.reposiroty.RefreshTokenRepository;
import org.musicservice.demo.security.verification.MailService;
import org.musicservice.demo.service.yandexCloud.properties.YandexStorageProperties;
import org.musicservice.demo.support.config.AbstractIntegrationTest;
import org.musicservice.demo.support.factory.cookie.CookieDataFactory;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    @Autowired
    private RefreshTokenCryptoService refreshTokenCryptoService;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private VerificationTokenProperties verificationTokenProperties;
    @Autowired
    private RefreshTokenProperties refreshTokenProperties;
    @MockitoBean
    private MailService mailService;

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

        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> activationLinkCaptor = ArgumentCaptor.forClass(String.class);
        verify(mailService).sendActivationEmail(emailCaptor.capture(), activationLinkCaptor.capture());
        String email = emailCaptor.getValue();
        String activationLink = activationLinkCaptor.getValue();

        assertThat(email).isEqualTo(user.getEmail());
        assertThat(activationLink.startsWith(verificationTokenProperties.getActivationUrl())).isTrue();
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

        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();

        assertThat(yandexStorageProperties.getDefaultAvatarKey()).isNotEqualTo(user.getUserAvatar().getKey());
        assertThat(request.getUsername()).isEqualTo(user.getUsername());
        assertThat(request.getEmail()).isEqualTo(user.getEmail());
        assertThat(request.getDateOfBirth()).isEqualTo(user.getDateOfBirth());
        assertThat(passwordEncoder.matches(request.getPassword(), user.getPassword())).isTrue();
        assertThat(user.isEnabled()).isFalse();

        Cookie cookie = result.getResponse().getCookie(cookieProperties.getRefreshTokenName());
        assertThat(cookie).isNotNull();
        assertThat(cookie.getValue()).isNotBlank();
        assertThat(cookie.isHttpOnly()).isEqualTo(cookieProperties.getHttpOnly());
        assertThat(cookie.getSecure()).isEqualTo(cookieProperties.getSecure());
        assertThat(cookie.getPath()).isEqualTo(cookieProperties.getPath());

        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> activationLinkCaptor = ArgumentCaptor.forClass(String.class);
        verify(mailService).sendActivationEmail(emailCaptor.capture(), activationLinkCaptor.capture());
        String email = emailCaptor.getValue();
        String activationLink = activationLinkCaptor.getValue();

        assertThat(email).isEqualTo(user.getEmail());
        assertThat(activationLink.startsWith(verificationTokenProperties.getActivationUrl())).isTrue();
    }

    @Test
    void shouldThrowRegistrationException_WhenUsernameAlreadyExists() throws Exception{
        RegistrationRequest request = ValidUserDataFactory.registrationRequest();
        userRepository.save(ValidUserDataFactory.userWithUsernameAlreadyExistsByRegistrationRequest(request, passwordEncoder));

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
        verifyNoInteractions(mailService);
    }

    @Test
    void shouldThrowRegistrationException_WhenEmailAlreadyExists() throws Exception{
        RegistrationRequest request = ValidUserDataFactory.registrationRequest();
        userRepository.save(ValidUserDataFactory.userWithEmailAlreadyExistsByRegistrationRequest(request, passwordEncoder));

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
        verifyNoInteractions(mailService);
    }

    @Test
    void shouldSuccessLoginAndReturnAccessToken() throws Exception{
        User user = userRepository.save(ValidUserDataFactory.userWithoutIdAndEnabledAccount(passwordEncoder));
        LoginRequest loginRequest = ValidUserDataFactory.loginRequest(user);

        String refreshTokenValue = refreshTokenCryptoService.generateRefreshToken();
        String hash = refreshTokenCryptoService.hash(refreshTokenValue);
        Duration refreshTokenDuration = refreshTokenProperties.getDuration();
        refreshTokenRepository.save(new RefreshToken(hash, Instant.now().plus(refreshTokenDuration), user.getId()));

        Cookie cookieRequest = CookieDataFactory.cookie(cookieProperties, (int) refreshTokenProperties.getDuration().getSeconds(), refreshTokenValue);

        String loginRequestJson = objectMapper.writeValueAsString(loginRequest);

        MvcResult result = mockMvc.perform(post("/api/auth/login").cookie(cookieRequest)
                .content(loginRequestJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        TokenResponse tokenResponse = objectMapper.readValue(jsonResult, TokenResponse.class);
        assertThat(tokenResponse.accessToken()).isNotBlank();

        Cookie cookieResponse = result.getResponse().getCookie(cookieProperties.getRefreshTokenName());
        assertThat(cookieResponse).isNotNull();
        assertThat(cookieResponse.getValue()).isNotEqualTo(cookieRequest.getValue());

        assertThat(refreshTokenRepository.findByTokenHash(hash)).isEmpty();
    }

    @Test
    void shouldFailedLogin_WhenUsernameInvalid() throws Exception{
        User user = userRepository.save(ValidUserDataFactory.userWithoutIdAndEnabledAccount(passwordEncoder));
        LoginRequest loginRequest = ValidUserDataFactory.loginRequest(user);
        loginRequest.setUsername("Michael45");

        String refreshTokenValue = refreshTokenCryptoService.generateRefreshToken();
        String hash = refreshTokenCryptoService.hash(refreshTokenValue);
        Duration refreshTokenDuration = refreshTokenProperties.getDuration();
        refreshTokenRepository.save(new RefreshToken(hash, Instant.now().plus(refreshTokenDuration), user.getId()));

        Cookie cookieRequest = CookieDataFactory.cookie(cookieProperties, (int) refreshTokenProperties.getDuration().getSeconds(), refreshTokenValue);

        String loginRequestJson = objectMapper.writeValueAsString(loginRequest);

        MvcResult result = mockMvc.perform(post("/api/auth/login").cookie(cookieRequest)
                        .content(loginRequestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();

        ApiErrorResponse errorResponseBody = objectMapper.readValue(jsonResult, ApiErrorResponse.class);

        assertThat(errorResponseBody.code()).isEqualTo(AuthErrorCode.BAD_CREDENTIALS.name());
        assertThat(errorResponseBody.message()).isEqualTo(AuthErrorCode.BAD_CREDENTIALS.getMessage());
        assertThat(errorResponseBody.status()).isEqualTo(HttpStatus.UNAUTHORIZED.value());

        Cookie cookieResponse = result.getResponse().getCookie(cookieProperties.getRefreshTokenName());
        assertThat(cookieResponse).isNull();
    }

    @Test
    void shouldFailedLogin_WhenPasswordInvalid() throws Exception{
        User user = userRepository.save(ValidUserDataFactory.userWithoutIdAndEnabledAccount(passwordEncoder));
        LoginRequest loginRequest = ValidUserDataFactory.loginRequest(user);
        loginRequest.setPassword("newPass1234");

        String refreshTokenValue = refreshTokenCryptoService.generateRefreshToken();
        String hash = refreshTokenCryptoService.hash(refreshTokenValue);
        Duration refreshTokenDuration = refreshTokenProperties.getDuration();
        refreshTokenRepository.save(new RefreshToken(hash, Instant.now().plus(refreshTokenDuration), user.getId()));

        Cookie cookieRequest = CookieDataFactory.cookie(cookieProperties, (int) refreshTokenProperties.getDuration().getSeconds(), refreshTokenValue);

        String loginRequestJson = objectMapper.writeValueAsString(loginRequest);

        MvcResult result = mockMvc.perform(post("/api/auth/login").cookie(cookieRequest)
                        .content(loginRequestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();

        ApiErrorResponse errorResponseBody = objectMapper.readValue(jsonResult, ApiErrorResponse.class);

        assertThat(errorResponseBody.code()).isEqualTo(AuthErrorCode.BAD_CREDENTIALS.name());
        assertThat(errorResponseBody.message()).isEqualTo(AuthErrorCode.BAD_CREDENTIALS.getMessage());
        assertThat(errorResponseBody.status()).isEqualTo(HttpStatus.UNAUTHORIZED.value());

        Cookie cookieResponse = result.getResponse().getCookie(cookieProperties.getRefreshTokenName());
        assertThat(cookieResponse).isNull();
    }

    @Test
    void shouldSuccessLogoutWithStatusIsOkAndClearRefreshTokenCookie() throws Exception{
        User user = userRepository.save(ValidUserDataFactory.userWithoutIdAndEnabledAccount(passwordEncoder));
        String refreshTokenValue = refreshTokenCryptoService.generateRefreshToken();
        String hash = refreshTokenCryptoService.hash(refreshTokenValue);
        Duration durationRefToken = refreshTokenProperties.getDuration();
        refreshTokenRepository.save(new RefreshToken(hash, Instant.now().plus(durationRefToken), user.getId()));

        Cookie cookie = CookieDataFactory.cookie(cookieProperties, (int) durationRefToken.getSeconds(), refreshTokenValue);

        MvcResult result = mockMvc.perform(post("/api/auth/logout").cookie(cookie))
                .andExpect(status().isOk())
                .andReturn();

        Cookie cookieResponse = result.getResponse().getCookie(cookieProperties.getRefreshTokenName());
        assertThat(cookieResponse).isNotNull();
        assertThat(cookieResponse.getValue()).isEmpty();
        assertThat(cookieResponse.getMaxAge()).isEqualTo(0);

        assertThat(refreshTokenRepository.findByTokenHash(hash)).isEmpty();
    }

    @Test
    void shouldReturnNewAccessTokenByUsedRefreshTokenCookieRequest() throws Exception{
        User user = userRepository.save(ValidUserDataFactory.userWithoutIdAndEnabledAccount(passwordEncoder));
        String refreshTokenValue = refreshTokenCryptoService.generateRefreshToken();
        String hash = refreshTokenCryptoService.hash(refreshTokenValue);
        Duration durationRefToken = refreshTokenProperties.getDuration();
        refreshTokenRepository.save(new RefreshToken(hash, Instant.now().plus(durationRefToken), user.getId()));

        Cookie cookie = CookieDataFactory.cookie(cookieProperties, (int) durationRefToken.getSeconds(), refreshTokenValue);

        MvcResult result = mockMvc.perform(post("/api/auth/refresh")
                .cookie(cookie))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        TokenResponse tokenResponse = objectMapper.readValue(responseJson, TokenResponse.class);
        assertThat(tokenResponse.accessToken()).isNotBlank();
    }




}
