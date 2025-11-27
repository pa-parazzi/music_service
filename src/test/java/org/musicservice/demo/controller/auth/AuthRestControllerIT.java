package org.musicservice.demo.controller.auth;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.musicservice.demo.service.security.RefreshTokenService;
import org.musicservice.demo.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class AuthRestControllerIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("MusicService")
            .withUsername("postgres")
            .withPassword("syperklik55");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @DynamicPropertySource
    static void setup(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.generate-ddl", ()-> true);
    }

    @BeforeEach
    void cleanup(){
        userService.deleteAll();
    }

    @Test
    @Transactional
    void registrationUserTest_ShouldSaveUserAndReturnsAccessToken() throws Exception{

        // given
        String userJson = """
                             {
                                "username": "TestUser",
                                "password": "test123",
                                "email": "igor.bocharov.88@gmail.com",
                                "dateOfBirth": "2001-01-01"
                             }
                        """;
        MockMultipartFile userPart = new MockMultipartFile(
                "user",
                "user.json",
                "application/json",
                userJson.getBytes()
        );

        // when

        mockMvc.perform(multipart("/api/auth/registration")
                .file(userPart)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt_token").exists());


        // then

        var user = userService.searchByEmail("igor.bocharov.88@gmail.com");
        var expectedRefToken = refreshTokenService.findByUserId(user.getId());

        assertEquals("TestUser", user.getUsername());
        assertEquals("default_avatar.jpg", user.getUserAvatar().getKey());
        assertEquals(expectedRefToken, user.getRefreshToken());
        assertNotEquals("test123", user.getPassword());
        assertFalse(user.isEnabled());
        
    }
}
