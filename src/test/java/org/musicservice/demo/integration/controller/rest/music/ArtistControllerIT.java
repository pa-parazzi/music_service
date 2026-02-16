package org.musicservice.demo.integration.controller.rest.music;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.musicservice.demo.dto.music.artist.ArtistResponse;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.exception.response.ApiErrorResponse;
import org.musicservice.demo.exception.response.ErrorType;
import org.musicservice.demo.repository.music.ArtistRepository;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ArtistControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ArtistRepository artistRepository;

    @BeforeEach
    void cleanupDb(){
        jdbcTemplate.execute("TRUNCATE TABLE artist RESTART IDENTITY CASCADE");
    }

    @Test
    void shouldReturnValidArtistResponseAndStatusIsOk() throws Exception{
        Artist artist = artistRepository.save(MusicFactoryIT.artist());

        MvcResult result = mockMvc.perform(get("/api/artist/{id}", artist.getId()))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        ArtistResponse actualResponse = objectMapper.readValue(jsonResult, ArtistResponse.class);
        assertThat(actualResponse.id()).isEqualTo(artist.getId());
        assertThat(actualResponse.name()).isEqualTo(artist.getName());
    }

    @Test
    void shouldReturnStatusIsNotFoundAndValidApiErrorResponse_WhenArtistIdInvalid() throws Exception {
        artistRepository.save(MusicFactoryIT.artist());

        MvcResult result = mockMvc.perform(get("/api/artist/{id}", 234L))
                .andExpect(status().isNotFound())
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        ApiErrorResponse errorResponse = objectMapper.readValue(jsonResult, ApiErrorResponse.class);
        assertThat(errorResponse.code()).isEqualTo(ErrorType.API_ERROR.name());
        assertThat(errorResponse.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
