package org.musicservice.demo.integration.controller.rest.music;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.musicservice.demo.dto.music.album.AlbumsResponse;
import org.musicservice.demo.dto.music.album.AlbumResponse;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.error.ApiErrorResponse;
import org.musicservice.demo.error.ErrorType;
import org.musicservice.demo.repository.image.AlbumImageRepository;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.musicservice.demo.repository.music.ArtistRepository;
import org.musicservice.demo.support.config.AbstractIntegrationTest;
import org.musicservice.demo.support.factory.it.music.MusicFactoryIT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @BeforeEach
    void cleanupDb(){
        jdbcTemplate.execute("TRUNCATE TABLE artist, album, album_image RESTART IDENTITY CASCADE");
    }

    @Test
    void shouldReturnValidMainAlbumResponseAndStatusIsOk() throws Exception {
        Artist artist = artistRepository.save(MusicFactoryIT.artist());
        Artist artist2 = artistRepository.save(MusicFactoryIT.artist2());
        Album album = albumRepository.save(MusicFactoryIT.album(artist));
        Album album2 = albumRepository.save(MusicFactoryIT.album2(artist2));
        Album album3 = albumRepository.save(MusicFactoryIT.album3(artist));
        List<Album> albums = List.of(album, album2, album3);
        albums.forEach(el -> el.setImage(albumImageRepository.save(MusicFactoryIT.albumImage(el))));
        Map<Long, Album> albumsByIdMap = albums.stream().collect(Collectors.toMap(Album::getId, Function.identity()));

        MvcResult result = mockMvc.perform(get("/api/album"))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        AlbumsResponse response = objectMapper.readValue(jsonResult, AlbumsResponse.class);
        List<AlbumResponse> albumResponseList = response.albums();
        albumResponseList.forEach(albumResponse -> assertAlbumResponse(albumResponse, albumsByIdMap.get(albumResponse.getAlbumId())));
    }

    @Test
    void shouldReturnValidAlbumResponseAndStatusIsOk() throws Exception {
        Artist artist = artistRepository.save(MusicFactoryIT.artist());
        Album album = albumRepository.save(MusicFactoryIT.album(artist));
        album.setImage(albumImageRepository.save(MusicFactoryIT.albumImage(album)));

        MvcResult result = mockMvc.perform(get("/api/album/{id}", album.getId()))
                .andExpect(status().isOk())
                .andReturn();

        String resultJson = result.getResponse().getContentAsString();
        AlbumResponse response = objectMapper.readValue(resultJson, AlbumResponse.class);
        assertAlbumResponse(response, album);
    }

    @Test
    void shouldReturnStatusIsNotFoundAndValidApiErrorResponse_WhenAlbumIdInvalid() throws Exception {
        Artist artist = artistRepository.save(MusicFactoryIT.artist());
        Album album = albumRepository.save(MusicFactoryIT.album(artist));
        album.setImage(albumImageRepository.save(MusicFactoryIT.albumImage(album)));

        MvcResult result = mockMvc.perform(get("/api/album/{id}", 126L))
                .andExpect(status().isNotFound())
                .andReturn();

        String resultJson = result.getResponse().getContentAsString();
        ApiErrorResponse errorResponse = objectMapper.readValue(resultJson, ApiErrorResponse.class);
        assertThat(errorResponse.code()).isEqualTo(ErrorType.API_ERROR.name());
        assertThat(errorResponse.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private void assertAlbumResponse(AlbumResponse response, Album album){
        assertThat(response.getAlbumId()).isEqualTo(album.getId());
        assertThat(response.getTitle()).isEqualTo(album.getTitle());
        assertThat(response.getArtist().id()).isEqualTo(album.getArtist().getId());
        assertThat(response.getArtist().name()).isEqualTo(album.getArtist().getName());
        assertThat(response.getAlbumImage().getKey()).isEqualTo(album.getImage().getKey());
    }

}
