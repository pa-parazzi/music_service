package org.musicservice.demo.controller.rest.music;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.musicservice.demo.dto.music.request.LikeRequest;
import org.musicservice.demo.dto.music.response.LikeResponse;
import org.musicservice.demo.factory.like.LikeFactory;
import org.musicservice.demo.factory.music.TestMusicDataFactory;
import org.musicservice.demo.factory.user.UserDataFactory;
import org.musicservice.demo.mapper.music.LikeResponseMapper;
import org.musicservice.demo.model.music.Album;
import org.musicservice.demo.model.music.Like;
import org.musicservice.demo.model.user.User;
import org.musicservice.demo.service.music.LikeService;
import org.musicservice.demo.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false, addFilters = false)
@Import({UserDataFactory.class, TestMusicDataFactory.class, LikeFactory.class})
public class LikeControllerIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("MusicService")
            .withUsername("postgres")
            .withPassword("syperklik55");

    @Autowired
    private LikeService likeService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDataFactory userDataFactory;

    @Autowired
    private LikeFactory likeFactory;

    @Autowired
    private TestMusicDataFactory musicDataFactory;

    @Autowired
    private LikeResponseMapper likeResponseMapper;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void cleanUpData(){
        likeFactory.cleanLikes();
        musicDataFactory.cleanData();
        userDataFactory.cleanUser();
    }

    @DynamicPropertySource
    static void setup(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.generate-ddl", ()-> true);
    }

    @Test
    void createLikeTest_ReturnValidLikeResponseFromUser() throws Exception {
        User user = userDataFactory.createUserFactory();
        Album album = musicDataFactory.createFactoryMusicData();
        Long userId = user.getId();
        Long albumId = album.getId();

        String likeJson = """
                {
                  "userId": %d,
                  "targetType": "album",
                  "targetId": %d
                }
                """.formatted(userId, albumId);

        MvcResult result = mockMvc.perform(post("/like")
                .contentType(MediaType.APPLICATION_JSON)
                .content(likeJson))
                .andExpect(status().isOk())
                .andReturn();

        var expected = likeResponseMapper.toResponse(likeService.findByUserId(userId));

        String json = result.getResponse().getContentAsString();
        LikeResponse actual = new ObjectMapper().readValue(json, LikeResponse.class);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);

    }

    @Test
    void getUserByThereLikes_ReturnUser() throws Exception {
        User expected = userDataFactory.createUserFactory();
        Album album = musicDataFactory.createFactoryMusicData();
        Long userId = expected.getId();
        Long albumId = album.getId();

        String likeJson = """
                {
                  "userId": %d,
                  "targetType": "album",
                  "targetId": %d
                }
                """.formatted(userId, albumId);

        MvcResult result = mockMvc.perform(post("/like")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(likeJson))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        LikeResponse response = new ObjectMapper().readValue(json, LikeResponse.class);

        var actual = userService.searchById(response.getUserId());

        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getUsername()).isEqualTo(expected.getUsername());
        assertThat(actual.getEmail()).isEqualTo(expected.getEmail());
    }

    @Test
    void deleteLikeByUserIdTest_ReturnAcceptedHttpStatus() throws Exception {
        User user = userDataFactory.createUserFactory();
        Album album = musicDataFactory.createFactoryMusicData();
        LikeRequest likeRequest = new LikeRequest();
        likeRequest.setUserId(user.getId());
        likeRequest.setTargetType("album");
        likeRequest.setTargetId(album.getId());
        Like like = likeFactory.createFactoryLike(likeRequest);

        String json = """
                {
                  "userId": %d,
                  "targetType": "album",
                  "targetId": %d
                }
                """.formatted(user.getId(), album.getId());


        mockMvc.perform(post("/like/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        assertThat(likeService.findOptByUserId(user.getId())).isEmpty();
    }
}
