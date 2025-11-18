package org.musicservice.demo.controller.auth;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.musicservice.demo.configuration.YandexCloud.YandexStorageProperties;
import org.musicservice.demo.repository.user.UserRepository;
import org.musicservice.demo.service.s3.S3UrlGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartResolver;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    private UserRepository userRepository;

    @DynamicPropertySource
    static void setup(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.generate-ddl", ()-> true);
    }

    @AfterEach
    void cleanup(){
        userRepository.deleteAll();
    }



    @Test
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

        MockMultipartFile avatarPart = new MockMultipartFile(
                "avatar",
                "avatar.jpg",
                "image/jpeg",
                new byte[0]
        );

        // when

        mockMvc.perform(multipart("/api/auth/registration")
                .file(userPart)
                .file(avatarPart)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt_token").exists());


        // then

        var users = userRepository.findAll();
        assertEquals(1, users.size());

        var savedUser = users.get(0);

        assertEquals("TestUser", savedUser.getUsername());
        assertEquals("igor.bocharov.88@gmail.com", savedUser.getEmail());
        assertFalse(savedUser.isEnabled());
        
    }
}
