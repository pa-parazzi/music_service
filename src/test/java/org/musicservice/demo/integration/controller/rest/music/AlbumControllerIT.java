package org.musicservice.demo.integration.controller.rest.music;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.musicservice.demo.dto.music.album.AlbumResponse;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.error.ApiErrorResponse;
import org.musicservice.demo.error.ErrorType;
import org.musicservice.demo.support.config.AbstractIntegrationTest;
import org.musicservice.demo.support.fixture.AlbumTestFixture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.musicservice.demo.support.assertions.ApiErrorAssertions.assertApiErrorResponse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@Import(AlbumTestFixture.class)
public class AlbumControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private AlbumTestFixture albumFixture;

    @BeforeEach
    void cleanupDb(){
        jdbcTemplate.execute("TRUNCATE TABLE genre, artist, album, album_image RESTART IDENTITY CASCADE");
    }

    @Test
    void shouldReturnValidAlbumResponse() throws Exception {
        Album album = albumFixture.albumAggregateWithOneAlbum().albums().getFirst();

        MvcResult result = mockMvc.perform(get("/api/album/{id}", album.getId()))
                .andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$.id").exists(),
                        jsonPath("$.title").exists(),
                        jsonPath("$.image").exists(),
                        jsonPath("$.artist").exists())
                .andReturn();

        String resultJson = result.getResponse().getContentAsString();
        AlbumResponse response = objectMapper.readValue(resultJson, AlbumResponse.class);

        assertThat(response.id()).isEqualTo(album.getId());
        assertThat(response.title()).isEqualTo(album.getTitle());
        assertThat(response.artist().id()).isEqualTo(album.getArtist().getId());
        assertThat(response.artist().name()).isEqualTo(album.getArtist().getName());
        assertThat(response.image().key()).isEqualTo(album.getImage().getKey());
    }

    @Test
    void shouldReturnApiErrorResponse_WhenAlbumIdInvalid() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/album/{id}", 126L))
                .andExpect(status().isNotFound())
                .andExpectAll(
                        jsonPath("$.code").exists(),
                        jsonPath("$.message").exists(),
                        jsonPath("$.status").exists(),
                        jsonPath("$.timestamp").exists())
                .andReturn();

        String resultJson = result.getResponse().getContentAsString();
        ApiErrorResponse errorResponse = objectMapper.readValue(resultJson, ApiErrorResponse.class);

        assertApiErrorResponse(errorResponse, ErrorType.API_ERROR, HttpStatus.NOT_FOUND);
    }

}
