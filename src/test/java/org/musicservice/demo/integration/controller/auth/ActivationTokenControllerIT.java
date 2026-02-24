package org.musicservice.demo.integration.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.musicservice.demo.dto.user.ResponseToEmailVerification;
import org.musicservice.demo.entity.auth.VerificationToken;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.exception.response.ApiErrorResponse;
import org.musicservice.demo.exception.response.VerificationTokenErrorCode;
import org.musicservice.demo.repository.user.UserRepository;
import org.musicservice.demo.security.properties.VerificationTokenProperties;
import org.musicservice.demo.security.reposiroty.VerificationTokenRepository;
import org.musicservice.demo.support.config.AbstractIntegrationTest;
import org.musicservice.demo.support.factory.it.verificationToken.VerificationTokenFactoryIT;
import org.musicservice.demo.support.factory.it.user.UserDataFactoryIT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class ActivationTokenControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private VerificationTokenProperties verificationTokenProperties;

    @BeforeEach
    void cleanupDb(){
        jdbcTemplate.execute("TRUNCATE TABLE users, verification_token RESTART IDENTITY CASCADE");
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

        String resultJson = result.getResponse().getContentAsString();
        ResponseToEmailVerification response = objectMapper.readValue(resultJson, ResponseToEmailVerification.class);
        assertThat(response.message()).isNotBlank();
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
}
