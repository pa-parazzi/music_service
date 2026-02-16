package org.musicservice.demo.integration.controller.rest.music;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.musicservice.demo.dto.music.album.AlbumResponse;
import org.musicservice.demo.dto.music.album.MainAlbumResponse;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.exception.response.ApiErrorResponse;
import org.musicservice.demo.exception.response.ErrorType;
import org.musicservice.demo.mapper.music.AlbumMapper;
import org.musicservice.demo.repository.image.AlbumImageRepository;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.musicservice.demo.repository.music.ArtistRepository;
import org.musicservice.demo.support.factory.it.music.MusicFactoryIT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AlbumControllerIT {

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
    private AlbumMapper albumMapper;

    @BeforeEach
    void cleanupDb(){
        jdbcTemplate.execute("TRUNCATE TABLE artist, album, album_image RESTART IDENTITY CASCADE");
    }

    @Test
    void shouldReturnValidMainAlbumResponseAndStatusIsOk() throws Exception {
        Artist artist = artistRepository.save(MusicFactoryIT.artist());
        List<Album> albumList = albumRepository.saveAll(MusicFactoryIT.albumList(artist));
        albumList.forEach(album -> album.setImage(albumImageRepository.save(MusicFactoryIT.albumImage(album))));
        MainAlbumResponse expectedResponse = new MainAlbumResponse(albumList.stream().map(albumMapper::toAlbumResponse).toList());

        MvcResult result = mockMvc.perform(get("/api/album"))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        MainAlbumResponse actualResponse = objectMapper.readValue(jsonResult, MainAlbumResponse.class);
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    void shouldReturnValidAlbumResponseAndStatusIsOk() throws Exception {
        Artist artist = artistRepository.save(MusicFactoryIT.artist());
        Album album = albumRepository.save(MusicFactoryIT.album(artist));
        album.setImage(albumImageRepository.save(MusicFactoryIT.albumImage(album)));
        AlbumResponse expectedAlbum = albumMapper.toAlbumResponse(album);

        MvcResult result = mockMvc.perform(get("/api/album/{id}", album.getId()))
                .andExpect(status().isOk())
                .andReturn();

        String resultJson = result.getResponse().getContentAsString();
        AlbumResponse actualResponse = objectMapper.readValue(resultJson, AlbumResponse.class);
        assertThat(actualResponse).isEqualTo(expectedAlbum);
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

}
