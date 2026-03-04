package org.musicservice.demo.integration.controller.rest.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.musicservice.demo.dto.user.UserMainResponse;
import org.musicservice.demo.entity.image.UserAvatar;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.error.ApiErrorResponse;
import org.musicservice.demo.error.auth.AuthErrorCode;
import org.musicservice.demo.repository.image.UserAvatarRepository;
import org.musicservice.demo.repository.user.UserRepository;
import org.musicservice.demo.security.dto.TokenSubject;
import org.musicservice.demo.security.jwt.JwtTokenService;
import org.musicservice.demo.support.config.AbstractIntegrationTest;
import org.musicservice.demo.support.factory.it.user.UserAvatarFactoryIT;
import org.musicservice.demo.support.factory.it.user.UserDataFactoryIT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class UserControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserAvatarRepository userAvatarRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenService jwtTokenService;

    @BeforeEach
    void cleanupDb(){
        jdbcTemplate.execute("TRUNCATE TABLE users, user_avatar RESTART IDENTITY CASCADE");
    }

    @Test
    void shouldReturnValidUserMainResponseAndStatusIsOk() throws Exception {
        User user = userRepository.save(UserDataFactoryIT.userWithEnabledAccount(passwordEncoder));
        UserAvatar userAvatar = userAvatarRepository.save(UserAvatarFactoryIT.userAvatar(user));
        String jwtToken = jwtTokenService.generateToken(new TokenSubject(user.getId(), List.of(user.getRole().getAuthority())));

        MvcResult result = mockMvc.perform(get("/user/profile")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andReturn();

        String resultJson = result.getResponse().getContentAsString();
        UserMainResponse response = objectMapper.readValue(resultJson, UserMainResponse.class);
        assertThat(response.getUsername()).isEqualTo(user.getUsername());
        assertThat(response.getAvatar().getKey()).isEqualTo(userAvatar.getKey());
    }

    @Test
    void shouldReturnStatusIsUnauthorized_WhenJwtTokenIsMissing() throws Exception {
        User user = userRepository.save(UserDataFactoryIT.userWithEnabledAccount(passwordEncoder));
        userAvatarRepository.save(UserAvatarFactoryIT.userAvatar(user));

        MvcResult result = mockMvc.perform(get("/user/profile"))
                .andExpect(status().isUnauthorized())
                .andReturn();

        String resultJson = result.getResponse().getContentAsString();
        ApiErrorResponse errorResponse = objectMapper.readValue(resultJson, ApiErrorResponse.class);
        assertApiErrorResponse(errorResponse);
    }

    @Test
    void shouldReturnStatusIsUnauthorized_WhenJwtTokenInvalid() throws Exception {
        User user = userRepository.save(UserDataFactoryIT.userWithEnabledAccount(passwordEncoder));
        userAvatarRepository.save(UserAvatarFactoryIT.userAvatar(user));
        String jwtToken = jwtTokenService.generateToken(new TokenSubject(user.getId(), List.of(user.getRole().getAuthority())));

        MvcResult result = mockMvc.perform(get("/user/profile")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken + "invalid.token"))
                .andExpect(status().isUnauthorized())
                .andReturn();

        String resultJson = result.getResponse().getContentAsString();
        ApiErrorResponse errorResponse = objectMapper.readValue(resultJson, ApiErrorResponse.class);
        assertApiErrorResponse(errorResponse);
    }

    private void assertApiErrorResponse(ApiErrorResponse errorResponse){
        assertThat(errorResponse.code()).isEqualTo(AuthErrorCode.BAD_AUTHENTICATION_REQUEST.name());
        assertThat(errorResponse.status()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
    }
}
