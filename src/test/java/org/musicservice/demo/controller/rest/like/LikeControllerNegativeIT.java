package org.musicservice.demo.controller.rest.like;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.musicservice.demo.factory.like.LikeFactory;
import org.musicservice.demo.factory.music.TestMusicDataFactory;
import org.musicservice.demo.factory.user.UserDataFactory;
import org.musicservice.demo.mapper.like.LikeResponseMapper;
import org.musicservice.demo.model.music.Album;
import org.musicservice.demo.model.music.Like;
import org.musicservice.demo.model.music.Sound;
import org.musicservice.demo.model.user.User;
import org.musicservice.demo.service.music.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false, addFilters = false)
@Import({UserDataFactory.class, TestMusicDataFactory.class, LikeFactory.class})
public class LikeControllerNegativeIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("MusicService")
            .withUsername("postgres")
            .withPassword("syperklik55");


    @Autowired
    private LikeService likeService;

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
    void getAlbumLikesByUserRequest_ReturnHttpStatusNotFound() throws Exception {
        User user = userDataFactory.createUserFactory();
        Album album = musicDataFactory.createFactoryMusicData();
        String targetType = "sound";
        List<Like> likes = new ArrayList<>();
        List<Sound> soundList = album.getSoundList();
        for(Sound sound: soundList){
            likes.add(likeFactory.createFactoryLike(user, sound.getId(), targetType));
        }

        String json = """
                {
                  "userId": %d
                }
                """.formatted(user.getId());

        mockMvc.perform(post("/like/get/albumLikes")
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isNotFound())
                .andReturn();
    }
}
