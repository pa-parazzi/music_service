package org.musicservice.demo.integration.controller.rest.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.musicservice.demo.dto.user.UserMainResponse;
import org.musicservice.demo.entity.image.UserAvatar;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.mapper.user.UserMapper;
import org.musicservice.demo.repository.image.UserAvatarRepository;
import org.musicservice.demo.repository.user.UserRepository;
import org.musicservice.demo.security.dto.TokenSubject;
import org.musicservice.demo.security.jwt.JwtTokenService;
import org.musicservice.demo.support.factory.it.user.UserAvatarFactoryIT;
import org.musicservice.demo.support.factory.user.ValidUserDataFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIT {

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
    private UserMapper userMapper;
    @Autowired
    private JwtTokenService jwtTokenService;

    @BeforeEach
    void cleanupDb(){
        jdbcTemplate.execute("TRUNCATE TABLE users, images_avatar RESTART IDENTITY CASCADE");
    }

    @Test
    void shouldReturnValidUserMainResponseAndStatusIsOk() throws Exception {
        User user = userRepository.save(ValidUserDataFactory.userWithoutIdAndEnabledAccount(passwordEncoder));
        UserAvatar userAvatar = userAvatarRepository.save(UserAvatarFactoryIT.userAvatar(user));
        user.setUserAvatar(userAvatar);
        UserMainResponse expectedUserResponse = userMapper.toMainResponse(user);
        String jwtToken = jwtTokenService.generateToken(new TokenSubject(user.getId(), List.of(user.getRole().getAuthority())));

        MvcResult result = mockMvc.perform(get("/user/profile")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andReturn();

        String resultJson = result.getResponse().getContentAsString();
        UserMainResponse actualUserResponse = objectMapper.readValue(resultJson, UserMainResponse.class);
        assertThat(actualUserResponse).isEqualTo(expectedUserResponse);
    }


}
