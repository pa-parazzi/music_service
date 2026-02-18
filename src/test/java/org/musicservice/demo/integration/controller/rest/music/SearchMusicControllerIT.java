package org.musicservice.demo.integration.controller.rest.music;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.musicservice.demo.dto.music.album.AlbumResponse;
import org.musicservice.demo.dto.music.artist.ArtistResponse;
import org.musicservice.demo.dto.music.search.SearchMusicResponse;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.exception.response.ApiErrorResponse;
import org.musicservice.demo.exception.response.ErrorType;
import org.musicservice.demo.mapper.music.AlbumMapper;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SearchMusicControllerIT extends AbstractIntegrationTest {

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
        Artist artist = artistRepository.save(MusicFactoryIT.artist());
        List<Album> albums = albumRepository.saveAll(MusicFactoryIT.albumList(artist));
        albums.forEach(album -> album.setImage(albumImageRepository.save(MusicFactoryIT.albumImage(album))));
    }

    @Test
    void shouldReturnSearchResultWithArtistsAndAlbums() throws Exception{
        String fragment = "M";
        List<ArtistResponse> foundArtists = artistRepository.findAllByNameStartingWith(fragment);
        List<AlbumResponse> foundAlbums = albumRepository.findAllByTitleStartingWith(fragment).stream().map(albumMapper::toAlbumResponse).toList();

        MvcResult result = mockMvc.perform(get("/search").param("fragment", fragment))
                .andExpect(status().isOk())
                .andReturn();

        String resultJson = result.getResponse().getContentAsString();
        SearchMusicResponse searchMusicResponse = objectMapper.readValue(resultJson, SearchMusicResponse.class);
        assertThat(searchMusicResponse.artists()).isEqualTo(foundArtists);
        assertThat(searchMusicResponse.albums()).isEqualTo(foundAlbums);
    }

    @Test
    void shouldReturnSearchResultWithOnlyArtistResponse_WhenAlbumsNotFound() throws Exception{
        String fragment = "Muse";
        List<ArtistResponse> foundArtists = artistRepository.findAllByNameStartingWith(fragment);
        MvcResult result = mockMvc.perform(get("/search").param("fragment", fragment))
                .andExpect(status().isOk())
                .andReturn();

        String resultJson = result.getResponse().getContentAsString();
        SearchMusicResponse searchMusicResponse = objectMapper.readValue(resultJson, SearchMusicResponse.class);
        assertThat(searchMusicResponse.artists()).isEqualTo(foundArtists);
        assertThat(searchMusicResponse.albums()).isEmpty();
    }

    @Test
    void shouldReturnSearchResultWithOnlyAlbumResponse_WhenArtistsNotFound() throws Exception{
        String fragment = "Tri Face";
        List<AlbumResponse> foundAlbums = albumRepository.findAllByTitleStartingWith(fragment).stream().map(albumMapper::toAlbumResponse).toList();
        MvcResult result = mockMvc.perform(get("/search").param("fragment", fragment))
                .andExpect(status().isOk())
                .andReturn();

        String resultJson = result.getResponse().getContentAsString();
        SearchMusicResponse searchMusicResponse = objectMapper.readValue(resultJson, SearchMusicResponse.class);
        assertThat(searchMusicResponse.artists()).isEmpty();
        assertThat(searchMusicResponse.albums()).isEqualTo(foundAlbums);
    }

    @Test
    void shouldReturnApiErrorResponse_WhenFragmentIsEmpty() throws Exception{
        String fragment = " ";

        MvcResult result = mockMvc.perform(get("/search").param("fragment", fragment))
                .andExpect(status().isNoContent())
                .andReturn();

        String resultJson = result.getResponse().getContentAsString();
        ApiErrorResponse errorResponse = objectMapper.readValue(resultJson, ApiErrorResponse.class);
        assertThat(errorResponse.code()).isEqualTo(ErrorType.INVALID_MUSIC_CONTENT.name());
        assertThat(errorResponse.status()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void shouldReturnApiErrorResponse_WhenFragmentIsNull() throws Exception{
        String fragment = null;

        MvcResult result = mockMvc.perform(get("/search").param("fragment", fragment))
                .andExpect(status().isNoContent())
                .andReturn();

        String resultJson = result.getResponse().getContentAsString();
        ApiErrorResponse errorResponse = objectMapper.readValue(resultJson, ApiErrorResponse.class);
        assertThat(errorResponse.code()).isEqualTo(ErrorType.INVALID_MUSIC_CONTENT.name());
        assertThat(errorResponse.status()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
