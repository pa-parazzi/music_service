package org.musicservice.demo.integration.controller.rest.music;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.musicservice.demo.dto.music.artist.ArtistResponse;
import org.musicservice.demo.entity.genre.Genre;
import org.musicservice.demo.entity.genre.GenreName;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.error.ApiErrorResponse;
import org.musicservice.demo.error.ErrorType;
import org.musicservice.demo.repository.music.GenreRepository;
import org.musicservice.demo.support.config.AbstractSpringBootIT;
import org.musicservice.demo.support.fixture.integration.ArtistTestFixture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.musicservice.demo.support.assertions.ApiErrorAssertions.assertApiErrorResponse;
import static org.musicservice.demo.support.assertions.ApiErrorAssertions.assertApiErrorResponseStructure;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(ArtistTestFixture.class)
public class ArtistControllerIT extends AbstractSpringBootIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ArtistTestFixture artistFixture;
    @Autowired
    private GenreRepository genreRepository;

    @BeforeEach
    void cleanupDb(){
        jdbcTemplate.execute("TRUNCATE TABLE artist RESTART IDENTITY CASCADE");
    }

    private Genre genre;

    @BeforeAll
    void getGenre(){
        genre = genreRepository.findByName(GenreName.ROCK).orElseThrow();
    }

    @Test
    void shouldReturnValidArtistResponseAndStatusIsOk() throws Exception{
        Artist artist = artistFixture.createArtist(genre);

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
        RequestBuilder requestBuilder = get("/api/artist/{id}", 234326L);
        MvcResult result = assertApiErrorResponseStructure(mockMvc.perform(requestBuilder), status().isNotFound());

        String jsonResult = result.getResponse().getContentAsString();
        ApiErrorResponse errorResponse = objectMapper.readValue(jsonResult, ApiErrorResponse.class);
        assertApiErrorResponse(errorResponse, ErrorType.API_ERROR, HttpStatus.NOT_FOUND);
    }
}
