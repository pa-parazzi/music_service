package org.musicservice.demo.integration.controller.rest.music;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.musicservice.demo.dto.music.artist.ArtistResponse;
import org.musicservice.demo.entity.genre.Genre;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.error.ApiErrorResponse;
import org.musicservice.demo.error.ErrorType;
import org.musicservice.demo.repository.music.ArtistRepository;
import org.musicservice.demo.repository.music.GenreRepository;
import org.musicservice.demo.support.config.AbstractIntegrationTest;
import org.musicservice.demo.support.factory.it.music.MusicFactoryIT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ArtistControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private GenreRepository genreRepository;

    @BeforeEach
    void cleanupDb(){
        jdbcTemplate.execute("TRUNCATE TABLE genre, artist RESTART IDENTITY CASCADE");
    }

    @Test
    void shouldReturnValidArtistResponseAndStatusIsOk() throws Exception{
        Genre genre = genreRepository.save(MusicFactoryIT.genre());
        Artist artist = artistRepository.save(MusicFactoryIT.artist(genre));

        MvcResult result = mockMvc.perform(get("/api/artist/{id}", artist.getId()))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.id").exists(),
                        jsonPath("$.name").exists()
                )
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        ArtistResponse actualResponse = objectMapper.readValue(jsonResult, ArtistResponse.class);
        assertThat(actualResponse.id()).isEqualTo(artist.getId());
        assertThat(actualResponse.name()).isEqualTo(artist.getName());
    }

    @Test
    void shouldReturnStatusIsNotFoundAndValidApiErrorResponse_WhenArtistIdInvalid() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/artist/{id}", 234326L))
                .andExpect(status().isNotFound())
                .andExpectAll(
                        jsonPath("$.code").exists(),
                        jsonPath("$.message").exists(),
                        jsonPath("$.status").exists(),
                        jsonPath("$.timestamp").exists())
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        ApiErrorResponse errorResponse = objectMapper.readValue(jsonResult, ApiErrorResponse.class);
        assertThat(errorResponse.code()).isEqualTo(ErrorType.API_ERROR.name());
        assertThat(errorResponse.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(errorResponse.message()).isNotBlank();
        assertThat(errorResponse.timestamp()).isPositive();
    }
}
