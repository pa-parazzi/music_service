package org.musicservice.demo.controller.rest.music;

import org.junit.jupiter.api.Test;
import org.musicservice.demo.dto.music.mainResponse.AlbumResponse;
import org.musicservice.demo.service.music.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.core.type.TypeReference;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
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
    private AlbumService albumService;

    @Autowired
    private MockMvc mockMvc;

    // TODO: Доделать тест
    @Test
    void searchAlbumResponseTest_ReturnAlbumResponseByTitleStartingWithFragment() throws Exception {
        final String fragment = "Tri";
        List<AlbumResponse> actual = albumService.findAlbumResponseStartingWith(fragment);
        
        MvcResult result = mockMvc.perform(post("/search").param("fragment", fragment))
                .andExpect(status().isOk())
                .andReturn();
        
        String json = result.getResponse().getContentAsString();
        List<AlbumResponse> excepted = new ObjectMapper().readValue(json, new TypeReference<List<AlbumResponse>>() {});

        assertThat(actual).usingRecursiveComparison().isEqualTo(excepted);
        assertNotNull(result);
    }



}
