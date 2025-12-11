package org.musicservice.demo.controller.rest.music;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.musicservice.demo.dto.music.response.AlbumResponse;
import org.musicservice.demo.dto.music.response.MainResponse;
import org.musicservice.demo.dto.music.response.SearchArtistAndAlbumResponse;
import org.musicservice.demo.factory.music.TestMusicDataFactory;
import org.musicservice.demo.model.music.Album;
import org.musicservice.demo.service.music.AlbumService;
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
import org.testcontainers.shaded.com.fasterxml.jackson.core.type.TypeReference;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@Import({TestMusicDataFactory.class})
public class SearchMusicControllerIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("MusicService")
            .withUsername("postgres")
            .withPassword("syperklik55");

    @DynamicPropertySource
    static void setup(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.generate-ddl", ()-> true);
    }

    @Autowired
    private TestMusicDataFactory musicDataFactory;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void cleanData(){
        musicDataFactory.cleanData();
    }

    @Test
    void searchAlbumResponseTest_ReturnAlbumResponseByTitleStartingWithFragment() throws Exception {
        Album album = musicDataFactory.createFactoryMusicData();
        final String fragment = album.getTitle();
        SearchArtistAndAlbumResponse response = musicDataFactory.getSearchArtistAndAlbumResponse(album);
        response.setArtists(Collections.emptyList());
        String expectedJson = new ObjectMapper().writeValueAsString(response);

        mockMvc.perform(post("/search").param("fragment", fragment))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson))
                .andReturn();
    }



}
