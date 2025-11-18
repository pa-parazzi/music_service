package org.musicservice.demo.controller.rest.music;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.musicservice.demo.cloud.CloudStorageClient;
import org.musicservice.demo.config.MockYandexStorageConfig;
import org.musicservice.demo.configuration.YandexCloud.YandexStorageProperties;
import org.musicservice.demo.dto.music.mainResponse.AlbumResponse;
import org.musicservice.demo.factory.TestMusicDataFactory;
import org.musicservice.demo.model.image.AlbumImage;
import org.musicservice.demo.model.music.Album;
import org.musicservice.demo.model.music.Artist;
import org.musicservice.demo.model.music.Sound;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.musicservice.demo.repository.music.ArtistRepository;
import org.musicservice.demo.service.music.MusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;


@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@Import(TestMusicDataFactory.class)
class AlbumControllerTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("MusicService")
            .withUsername("postgres")
            .withPassword("syperklik55");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestMusicDataFactory testMusicDataFactory;

    @DynamicPropertySource
    static void setup(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.generate-ddl", ()-> true);
    }

    @BeforeEach
    void cleanup(){
        testMusicDataFactory.cleanData();
    }

    @Transactional
    @Test
    void mainResponseAlbumsTest_ReturnsValidHttpStatusAndAlbums() throws Exception{
        Album album = testMusicDataFactory.createFactoryMusicData();
        List<AlbumResponse> albumResponses = Collections.singletonList(testMusicDataFactory.getAlbumResponseByFactoryMusicData(album));

        mockMvc.perform(get("/api/album"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.albums[0].title").value(albumResponses.getFirst().getTitle()))
                .andExpect(jsonPath("$.albums[0].artist.name").value(albumResponses.getFirst().getArtist().getName()));
    }

}