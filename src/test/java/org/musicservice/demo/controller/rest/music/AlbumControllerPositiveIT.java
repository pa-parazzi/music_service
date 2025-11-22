package org.musicservice.demo.controller.rest.music;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.musicservice.demo.dto.music.mainResponse.AlbumResponse;
import org.musicservice.demo.dto.music.mainResponse.MainResponse;
import org.musicservice.demo.factory.TestMusicDataFactory;
import org.musicservice.demo.model.music.Album;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@Import(TestMusicDataFactory.class)
class AlbumControllerPositiveIT {

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
    void viewAlbumsTest_ReturnsValidHttpStatusAndAlbumsData() throws Exception{
        // given
        Album album = testMusicDataFactory.createFactoryMusicData();
        MainResponse expected = testMusicDataFactory.getFactoryMainResponse(album);

        // when
        MvcResult result = mockMvc.perform(get("/api/album"))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        MainResponse actual = new ObjectMapper().readValue(json, MainResponse.class);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Transactional
    @Test
    void viewAlbumById_ReturnValidAlbum() throws Exception{
        // given
        Album album = testMusicDataFactory.createFactoryMusicData();
        AlbumResponse expected = testMusicDataFactory.getAlbumResponseByFactoryMusicData(album);

        // when
        MvcResult result = mockMvc.perform(get("/api/album/{id}", expected.getAlbumId()))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        AlbumResponse actual = new ObjectMapper().readValue(json, AlbumResponse.class);

        // then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

}