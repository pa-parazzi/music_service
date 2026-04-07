package org.musicservice.demo.integration.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.musicservice.demo.dto.user.LoginRequest;
import org.musicservice.demo.dto.user.RegistrationRequest;
import org.musicservice.demo.entity.auth.RefreshToken;
import org.musicservice.demo.entity.auth.VerificationToken;
import org.musicservice.demo.entity.image.UserAvatar;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.error.ApiErrorResponse;
import org.musicservice.demo.error.ErrorType;
import org.musicservice.demo.error.auth.AuthErrorCode;
import org.musicservice.demo.error.auth.RefreshTokenErrorCode;
import org.musicservice.demo.error.auth.VerificationTokenErrorCode;
import org.musicservice.demo.error.user.UniqueFieldErrorCode;
import org.musicservice.demo.repository.image.UserAvatarRepository;
import org.musicservice.demo.repository.user.UserRepository;
import org.musicservice.demo.security.cookie.CookieProperties;
import org.musicservice.demo.security.dto.TokenResponse;
import org.musicservice.demo.security.properties.RefreshTokenProperties;
import org.musicservice.demo.security.properties.VerificationTokenProperties;
import org.musicservice.demo.security.refreshToken.RefreshTokenCryptoService;
import org.musicservice.demo.security.refreshToken.RefreshTokenRepository;
import org.musicservice.demo.security.verificationToken.MailService;
import org.musicservice.demo.security.verificationToken.VerificationTokenRepository;
import org.musicservice.demo.storage.s3.YandexStorageProperties;
import org.musicservice.demo.support.config.AbstractIntegrationTest;
import org.musicservice.demo.support.factory.it.cookie.CookieDataFactoryIT;
import org.musicservice.demo.support.factory.it.multipartFile.MultipartFileFactory;
import org.musicservice.demo.support.factory.it.refreshToken.RefreshTokenFactoryIT;
import org.musicservice.demo.support.factory.it.user.UserDataFactoryIT;
import org.musicservice.demo.support.factory.it.verificationToken.VerificationTokenFactoryIT;
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

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class AuthenticationControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserAvatarRepository userAvatarRepository;
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
    private VerificationTokenRepository verificationTokenRepository;
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
        RegistrationRequest registrationRequest = UserDataFactoryIT.registrationRequest();
        String userJson = objectMapper.writeValueAsString(registrationRequest);

        MockMultipartFile userPart = MultipartFileFactory.userPart(userJson);

        MvcResult result = mockMvc.perform(multipart("/api/auth/registration")
                .file(userPart)
                .contentType(contentMultipartForm))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        TokenResponse tokenResponse = objectMapper.readValue(jsonResult, TokenResponse.class);
        assertThatAccessTokenIsNotNull(tokenResponse);

        Cookie cookieResponse = result.getResponse().getCookie(cookieProperties.getRefreshTokenName());
        assertCookieResponse(cookieResponse);

        User user = userRepository.findByUsername(registrationRequest.getUsername()).orElseThrow();
        assertRegistrationFieldsUser(registrationRequest, user);

        UserAvatar userAvatar = userAvatarRepository.findByUserId(user.getId());
        assertThat(userAvatar.getKey()).isEqualTo(yandexStorageProperties.getDefaultAvatarKey());

        assertActivationEmailSent(user);
    }

    @Test
    void shouldRegisterUserWithNewAvatarAndReturnAccessToken_WhenMultipartFileFromUserAvatarIsPresent() throws Exception{
        RegistrationRequest request = UserDataFactoryIT.registrationRequest();
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
        TokenResponse tokenResponse = objectMapper.readValue(jsonResult, TokenResponse.class);
        assertThatAccessTokenIsNotNull(tokenResponse);

        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        assertRegistrationFieldsUser(request, user);

        UserAvatar userAvatar = userAvatarRepository.findByUserId(user.getId());
        assertThat(userAvatar.getKey()).isNotEqualTo(yandexStorageProperties.getDefaultAvatarKey());

        Cookie cookie = result.getResponse().getCookie(cookieProperties.getRefreshTokenName());
        assertCookieResponse(cookie);

        assertActivationEmailSent(user);
    }

    @Test
    void shouldThrowRegistrationException_WhenUsernameAlreadyExists() throws Exception{
        RegistrationRequest request = UserDataFactoryIT.registrationRequest();
        userRepository.save(UserDataFactoryIT.userWithUsernameAlreadyExistsByRegistrationRequest(request, passwordEncoder));

        String userJson = objectMapper.writeValueAsString(request);
        MockMultipartFile userPart = MultipartFileFactory.userPart(userJson);

        MvcResult result = mockMvc.perform(multipart("/api/auth/registration")
                .file(userPart)
                .contentType(contentMultipartForm))
                .andExpect(status().isBadRequest())
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        ApiErrorResponse errorResponse = objectMapper.readValue(jsonResult, ApiErrorResponse.class);
        assertApiErrorResponseWhenRegistrationError(errorResponse, UniqueFieldErrorCode.USERNAME.getField());

        verifyNoInteractions(mailService);
    }

    @Test
    void shouldThrowRegistrationException_WhenEmailAlreadyExists() throws Exception{
        RegistrationRequest request = UserDataFactoryIT.registrationRequest();
        userRepository.save(UserDataFactoryIT.userWithEmailAlreadyExistsByRegistrationRequest(request, passwordEncoder));

        String userJson = objectMapper.writeValueAsString(request);
        MockMultipartFile userPart = MultipartFileFactory.userPart(userJson);

        MvcResult result = mockMvc.perform(multipart("/api/auth/registration")
                        .file(userPart)
                        .contentType(contentMultipartForm))
                .andExpect(status().isBadRequest())
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        ApiErrorResponse errorResponse = objectMapper.readValue(jsonResult, ApiErrorResponse.class);
        assertApiErrorResponseWhenRegistrationError(errorResponse, UniqueFieldErrorCode.EMAIL.getField());

        verifyNoInteractions(mailService);
    }

    @Test
    void shouldSuccessLoginAndReturnAccessToken() throws Exception {
        User user = UserDataFactoryIT.user();
        String password = UUID.randomUUID().toString();
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true);
        userRepository.save(user);
        LoginRequest loginRequest = UserDataFactoryIT.loginRequest(user.getUsername(), password);
        String loginRequestJson = objectMapper.writeValueAsString(loginRequest);

        String refreshTokenValue = refreshTokenCryptoService.generateRefreshToken();
        RefreshToken usedRefreshToken = RefreshTokenFactoryIT.refreshToken(
                refreshTokenValue, user, refreshTokenProperties, refreshTokenCryptoService, refreshTokenRepository);

        Cookie cookieRequest = CookieDataFactoryIT.cookie(cookieProperties, (int) refreshTokenProperties.getDuration().getSeconds(), refreshTokenValue);

        MvcResult result = mockMvc.perform(post("/api/auth/login").cookie(cookieRequest)
                .content(loginRequestJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        TokenResponse tokenResponse = objectMapper.readValue(jsonResult, TokenResponse.class);
        assertThatAccessTokenIsNotNull(tokenResponse);

        Cookie cookieResponse = extractValidRefreshCookie(result);
        assertCookieResponse(cookieResponse);
        assertThat(cookieResponse.getValue()).isNotEqualTo(cookieRequest.getValue());

        RefreshToken actualRefreshToken = refreshTokenRepository.findByUserId(user.getId()).orElseThrow();
        assertThat(usedRefreshToken.getTokenHash()).isNotEqualTo(actualRefreshToken.getTokenHash());
    }

    @Test
    void shouldReturnApiErrorResponse_WhenAccountIsNotEnabled() throws Exception {
        User user = UserDataFactoryIT.user();
        String password = UUID.randomUUID().toString();
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        LoginRequest loginRequest = UserDataFactoryIT.loginRequest(user.getUsername(), password);
        String loginRequestJson = objectMapper.writeValueAsString(loginRequest);

        String refreshTokenValue = refreshTokenCryptoService.generateRefreshToken();
        RefreshTokenFactoryIT.refreshToken(
                refreshTokenValue, user, refreshTokenProperties, refreshTokenCryptoService, refreshTokenRepository);

        Cookie cookieRequest = CookieDataFactoryIT.cookie(cookieProperties, (int) refreshTokenProperties.getDuration().getSeconds(), refreshTokenValue);

        MvcResult result = mockMvc.perform(post("/api/auth/login").cookie(cookieRequest)
                        .content(loginRequestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        ApiErrorResponse errorResponse = objectMapper.readValue(jsonResult, ApiErrorResponse.class);
        assertThat(errorResponse.code()).isEqualTo(AuthErrorCode.ACCOUNT_NOT_ACTIVATED.name());
        assertThat(errorResponse.status()).isEqualTo(HttpStatus.UNAUTHORIZED.value());

        Cookie cookieResponse = result.getResponse().getCookie(cookieProperties.getRefreshTokenName());
        assertThat(cookieResponse).isNull();
    }

    @Test
    void shouldFailedLogin_WhenUsernameInvalid() throws Exception{
        User user = userRepository.save(UserDataFactoryIT.userWithEnabledAccount());
        LoginRequest loginRequest = UserDataFactoryIT.loginRequest("invalid username", user.getPassword());

        String refreshTokenValue = refreshTokenCryptoService.generateRefreshToken();
        RefreshTokenFactoryIT.refreshToken(
                refreshTokenValue, user, refreshTokenProperties, refreshTokenCryptoService, refreshTokenRepository);

        Cookie cookieRequest = CookieDataFactoryIT.cookie(cookieProperties, (int) refreshTokenProperties.getDuration().getSeconds(), refreshTokenValue);

        String loginRequestJson = objectMapper.writeValueAsString(loginRequest);

        MvcResult result = mockMvc.perform(post("/api/auth/login").cookie(cookieRequest)
                        .content(loginRequestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();

        ApiErrorResponse errorResponse = objectMapper.readValue(jsonResult, ApiErrorResponse.class);
        assertApiErrorResponseWhenBadCredentials(errorResponse);

        Cookie cookieResponse = result.getResponse().getCookie(cookieProperties.getRefreshTokenName());
        assertCookieResponseIsNull(cookieResponse);
    }

    @Test
    void shouldFailedLogin_WhenPasswordInvalid() throws Exception{
        User user = userRepository.save(UserDataFactoryIT.userWithEnabledAccount());
        LoginRequest loginRequest = UserDataFactoryIT.loginRequest(user.getUsername(), "newPass1234");

        String refreshTokenValue = refreshTokenCryptoService.generateRefreshToken();
        RefreshTokenFactoryIT.refreshToken(
                refreshTokenValue, user, refreshTokenProperties, refreshTokenCryptoService, refreshTokenRepository);

        Cookie cookieRequest = CookieDataFactoryIT.cookie(cookieProperties, (int) refreshTokenProperties.getDuration().getSeconds(), refreshTokenValue);

        String loginRequestJson = objectMapper.writeValueAsString(loginRequest);

        MvcResult result = mockMvc.perform(post("/api/auth/login").cookie(cookieRequest)
                        .content(loginRequestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();

        ApiErrorResponse errorResponseBody = objectMapper.readValue(jsonResult, ApiErrorResponse.class);
        assertApiErrorResponseWhenBadCredentials(errorResponseBody);

        Cookie cookieResponse = result.getResponse().getCookie(cookieProperties.getRefreshTokenName());
        assertCookieResponseIsNull(cookieResponse);
    }

    @Test
    void shouldSuccessLogoutWithStatusIsOkAndClearRefreshTokenCookie_WhenCookieRequestIsPresent() throws Exception{
        User user = userRepository.save(UserDataFactoryIT.userWithEnabledAccount());
        String refreshTokenValue = refreshTokenCryptoService.generateRefreshToken();
        RefreshToken refreshToken = RefreshTokenFactoryIT.refreshToken(
                refreshTokenValue, user, refreshTokenProperties, refreshTokenCryptoService, refreshTokenRepository);

        Cookie cookie = CookieDataFactoryIT.cookie(cookieProperties, (int) refreshTokenProperties.getDuration().getSeconds(), refreshTokenValue);

        MvcResult result = mockMvc.perform(post("/api/auth/logout").cookie(cookie))
                .andExpect(status().isOk())
                .andReturn();

        Cookie cookieResponse = result.getResponse().getCookie(cookieProperties.getRefreshTokenName());
        assertClearingCookie(cookieResponse);

        assertThat(refreshTokenRepository.findByTokenHash(refreshToken.getTokenHash())).isEmpty();
    }

    @Test
    void shouldSuccessLogoutWithStatusIsOkAndClearRefreshTokenCookie_WhenCookieRequestIsMissing() throws Exception{
        User user = userRepository.save(UserDataFactoryIT.userWithEnabledAccount());

        MvcResult result = mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andReturn();

        Cookie cookieResponse = result.getResponse().getCookie(cookieProperties.getRefreshTokenName());
        assertClearingCookie(cookieResponse);
        assertThat(refreshTokenRepository.findByUserId(user.getId())).isEmpty();
    }

    @Test
    void shouldReturnNewAccessTokenByUsedRefreshTokenCookieRequest_WhenCookieRequestIsValid() throws Exception{
        User user = userRepository.save(UserDataFactoryIT.userWithEnabledAccount());
        String refreshTokenValue = refreshTokenCryptoService.generateRefreshToken();
        RefreshTokenFactoryIT.refreshToken(
                refreshTokenValue, user, refreshTokenProperties, refreshTokenCryptoService, refreshTokenRepository);

        Cookie cookie = CookieDataFactoryIT.cookie(cookieProperties, (int) refreshTokenProperties.getDuration().getSeconds(), refreshTokenValue);

        MvcResult result = mockMvc.perform(post("/api/auth/refresh")
                .cookie(cookie))
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        TokenResponse tokenResponse = objectMapper.readValue(responseJson, TokenResponse.class);
        assertThatAccessTokenIsNotNull(tokenResponse);
    }

    @Test
    void shouldReturnStatusUnauthorized_WhenCookieRequestIsMissing() throws Exception{
        userRepository.save(UserDataFactoryIT.userWithEnabledAccount());

        MvcResult result = mockMvc.perform(post("/api/auth/refresh"))
                .andExpect(status().isUnauthorized())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        ApiErrorResponse errorResponse = objectMapper.readValue(responseJson, ApiErrorResponse.class);
        assertThat(errorResponse.status()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(errorResponse.code()).isEqualTo(RefreshTokenErrorCode.MISSING.name());
        assertThat(errorResponse.message()).isEqualTo(RefreshTokenErrorCode.MISSING.getMessage());
    }

    @Test
    void shouldReturnStatusUnauthorized_WhenCookieInvalid() throws Exception{
        User user = userRepository.save(UserDataFactoryIT.userWithEnabledAccount());
        String refreshTokenValue = refreshTokenCryptoService.generateRefreshToken();
        RefreshTokenFactoryIT.refreshToken(
                refreshTokenValue, user, refreshTokenProperties, refreshTokenCryptoService, refreshTokenRepository);

        Cookie cookie = CookieDataFactoryIT.cookie(cookieProperties,(int) refreshTokenProperties.getDuration().getSeconds(), "invalid value");

        MvcResult result = mockMvc.perform(post("/api/auth/refresh").cookie(cookie))
                .andExpect(status().isUnauthorized())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        ApiErrorResponse errorResponse = objectMapper.readValue(responseJson, ApiErrorResponse.class);
        assertThat(errorResponse.status()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(errorResponse.code()).isEqualTo(RefreshTokenErrorCode.INVALID.name());
        assertThat(errorResponse.message()).isEqualTo(RefreshTokenErrorCode.INVALID.getMessage());
    }

    @Test
    void shouldEnableUserAccountAndReturnResponseToEmailVerificationAndStatusIsOk() throws Exception {
        User user = userRepository.save(UserDataFactoryIT.userWithEncodedPassword(passwordEncoder));
        VerificationToken verificationToken = verificationTokenRepository.save(VerificationTokenFactoryIT.validVerificationToken(user, verificationTokenProperties));
        String token = verificationToken.getToken();

        MvcResult result = mockMvc.perform(get("/api/auth/activate").param("token", token))
                .andExpect(status().isOk())
                .andReturn();

        User userIsAfterActivate = userRepository.findById(user.getId()).orElseThrow();
        assertThat(userIsAfterActivate.isEnabled()).isTrue();

        assertThat(verificationTokenRepository.findByToken(token)).isEmpty();

        String response = result.getResponse().getContentAsString();
        assertThat(response).isNotBlank();
    }

    @Test
    void shouldReturnStatusIsUnauthorized_WhenTokenIsEmpty() throws Exception {
        User user = userRepository.save(UserDataFactoryIT.userWithEncodedPassword(passwordEncoder));
        String token = " ";

        MvcResult result = mockMvc.perform(get("/api/auth/activate").param("token", token))
                .andExpect(status().isUnauthorized())
                .andReturn();

        User userIsAfterActivate = userRepository.findById(user.getId()).orElseThrow();
        assertThat(userIsAfterActivate.isEnabled()).isFalse();

        String resultJson = result.getResponse().getContentAsString();
        ApiErrorResponse errorResponse = objectMapper.readValue(resultJson, ApiErrorResponse.class);
        assertThat(errorResponse.code()).isEqualTo(VerificationTokenErrorCode.MISSING.name());
        assertThat(errorResponse.status()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldReturnStatusIsUnauthorized_WhenTokenIsNull() throws Exception {
        User user = userRepository.save(UserDataFactoryIT.userWithEncodedPassword(passwordEncoder));

        MvcResult result = mockMvc.perform(get("/api/auth/activate"))
                .andExpect(status().isUnauthorized())
                .andReturn();

        User userIsAfterActivate = userRepository.findById(user.getId()).orElseThrow();
        assertThat(userIsAfterActivate.isEnabled()).isFalse();

        String resultJson = result.getResponse().getContentAsString();
        ApiErrorResponse errorResponse = objectMapper.readValue(resultJson, ApiErrorResponse.class);
        assertThat(errorResponse.code()).isEqualTo(VerificationTokenErrorCode.MISSING.name());
        assertThat(errorResponse.status()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void shouldReturnStatusIsUnauthorized_WhenTokenIsExpired() throws Exception {
        User user = userRepository.save(UserDataFactoryIT.userWithEncodedPassword(passwordEncoder));
        VerificationToken expiredVerificationToken = verificationTokenRepository.save(VerificationTokenFactoryIT.expiredVerificationToken(user));
        String token = expiredVerificationToken.getToken();

        MvcResult result = mockMvc.perform(get("/api/auth/activate").param("token", token))
                .andExpect(status().isUnauthorized())
                .andReturn();

        User userIsAfterActivate = userRepository.findById(user.getId()).orElseThrow();
        assertThat(userIsAfterActivate.isEnabled()).isFalse();

        String resultJson = result.getResponse().getContentAsString();
        ApiErrorResponse errorResponse = objectMapper.readValue(resultJson, ApiErrorResponse.class);
        assertThat(errorResponse.code()).isEqualTo(VerificationTokenErrorCode.EXPIRED.name());
        assertThat(errorResponse.status()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    private void assertThatAccessTokenIsNotNull(TokenResponse tokenResponse){
        assertThat(tokenResponse.accessToken()).isNotBlank();
    }

    private void assertCookieResponse(Cookie cookieResponse){
        assertThat(cookieResponse).isNotNull();
        assertThat(cookieResponse.getValue()).isNotBlank();
        assertThat(cookieResponse.isHttpOnly()).isEqualTo(cookieProperties.getHttpOnly());
        assertThat(cookieResponse.getSecure()).isEqualTo(cookieProperties.getSecure());
        assertThat(cookieResponse.getPath()).isEqualTo(cookieProperties.getPath());
    }

    private void assertRegistrationFieldsUser(RegistrationRequest request, User user){
        assertThat(request.getUsername()).isEqualTo(user.getUsername());
        assertThat(passwordEncoder.matches(request.getPassword(), user.getPassword())).isTrue();
        assertThat(request.getEmail()).isEqualTo(user.getEmail());
        assertThat(request.getDateOfBirth()).isEqualTo(user.getDateOfBirth());
        assertThat(user.isEnabled()).isFalse();
    }

    private void assertActivationEmailSent(User user){
        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> activationLinkCaptor = ArgumentCaptor.forClass(String.class);
        verify(mailService).sendActivationEmail(emailCaptor.capture(), activationLinkCaptor.capture());
        String email = emailCaptor.getValue();
        String activationLink = activationLinkCaptor.getValue();

        assertThat(email).isEqualTo(user.getEmail());
        assertThat(activationLink.startsWith(verificationTokenProperties.getActivationUrl())).isTrue();
    }

    private void assertApiErrorResponseWhenRegistrationError(ApiErrorResponse errorResponse, String field){
        assertThat(errorResponse.code()).isEqualTo(ErrorType.REGISTRATION_ERROR.name());
        assertThat(errorResponse.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorResponse.fieldsError().containsKey(field)).isTrue();
    }

    private void assertApiErrorResponseWhenBadCredentials(ApiErrorResponse errorResponse){
        assertThat(errorResponse.code()).isEqualTo(AuthErrorCode.BAD_CREDENTIALS.name());
        assertThat(errorResponse.message()).isEqualTo(AuthErrorCode.BAD_CREDENTIALS.getMessage());
        assertThat(errorResponse.status()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }

    private void assertCookieResponseIsNull(Cookie cookieResponse){
        assertThat(cookieResponse).isNull();
    }

    private void assertClearingCookie(Cookie cookieResponse){
        assertThat(cookieResponse).isNotNull();
        assertThat(cookieResponse.getValue()).isEmpty();
        assertThat(cookieResponse.getMaxAge()).isEqualTo(0);
    }

    private Cookie extractValidRefreshCookie(MvcResult result){
        return Arrays.stream(result.getResponse().getCookies())
                .filter(cookie -> cookie.getName().equals(cookieProperties.getRefreshTokenName()))
                .filter(cookie -> cookie.getMaxAge() > 0)
                .findFirst()
                .orElseThrow();
    }

}
