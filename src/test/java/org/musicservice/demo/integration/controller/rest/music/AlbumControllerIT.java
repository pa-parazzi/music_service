package org.musicservice.demo.integration.controller.rest.music;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.musicservice.demo.dto.music.album.AlbumResponse;
import org.musicservice.demo.entity.genre.Genre;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.error.ApiErrorResponse;
import org.musicservice.demo.error.ErrorType;
import org.musicservice.demo.repository.image.AlbumImageRepository;
import org.musicservice.demo.repository.music.AlbumRepository;
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
public class AlbumControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private AlbumRepository albumRepository;
    @Autowired
    private AlbumImageRepository albumImageRepository;
    @Autowired
    private GenreRepository genreRepository;

    @BeforeEach
    void cleanupDb(){
        jdbcTemplate.execute("TRUNCATE TABLE genre, artist, album, album_image RESTART IDENTITY CASCADE");
    }

    @Test
    void shouldReturnValidAlbumResponse() throws Exception {
        Genre genre = genreRepository.save(MusicFactoryIT.genre());
        Artist artist = artistRepository.save(MusicFactoryIT.artist(genre));
        Album album = albumRepository.save(MusicFactoryIT.album(artist, genre));
        album.setImage(albumImageRepository.save(MusicFactoryIT.albumImage(album)));

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
        Genre genre = genreRepository.save(MusicFactoryIT.genre());
        Artist artist = artistRepository.save(MusicFactoryIT.artist(genre));
        Album album = albumRepository.save(MusicFactoryIT.album(artist, genre));
        album.setImage(albumImageRepository.save(MusicFactoryIT.albumImage(album)));

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

        assertThat(errorResponse.code()).isEqualTo(ErrorType.API_ERROR.name());
        assertThat(errorResponse.message()).isNotBlank();
        assertThat(errorResponse.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(errorResponse.timestamp()).isPositive();
    }

}
