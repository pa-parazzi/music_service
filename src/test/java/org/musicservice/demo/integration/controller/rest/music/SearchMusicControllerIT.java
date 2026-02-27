package org.musicservice.demo.integration.controller.rest.music;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.musicservice.demo.dto.music.album.AlbumResponse;
import org.musicservice.demo.dto.music.artist.ArtistResponse;
import org.musicservice.demo.dto.music.search.SearchMusicResponse;
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

    @BeforeEach
    void cleanupDb(){
        jdbcTemplate.execute("TRUNCATE TABLE artist, album, album_image RESTART IDENTITY CASCADE");
    }

    @Test
    void shouldReturnSearchResultWithArtistsAndAlbums() throws Exception{
        Artist artist = artistRepository.save(MusicFactoryIT.artist());
        Album album = albumRepository.save(MusicFactoryIT.album2(artist));
        album.setImage(albumImageRepository.save(MusicFactoryIT.albumImage(album)));

        String fragment = "M";

        MvcResult result = mockMvc.perform(get("/search").param("fragment", fragment))
                .andExpect(status().isOk())
                .andReturn();

        String resultJson = result.getResponse().getContentAsString();
        SearchMusicResponse searchMusicResponse = objectMapper.readValue(resultJson, SearchMusicResponse.class);
        ArtistResponse artistResponse = searchMusicResponse.artists().getFirst();
        AlbumResponse albumResponse = searchMusicResponse.albums().getFirst();
        assertArtistResponse(artistResponse, artist);
        assertAlbumResponse(albumResponse, album);
    }

    @Test
    void shouldReturnSearchResultWithOnlyArtistResponse_WhenAlbumsNotFound() throws Exception{
        Artist artist = artistRepository.save(MusicFactoryIT.artist());
        Album album = albumRepository.save(MusicFactoryIT.album(artist));
        album.setImage(albumImageRepository.save(MusicFactoryIT.albumImage(album)));

        String fragment = "Muse";

        MvcResult result = mockMvc.perform(get("/search").param("fragment", fragment))
                .andExpect(status().isOk())
                .andReturn();

        String resultJson = result.getResponse().getContentAsString();
        SearchMusicResponse searchMusicResponse = objectMapper.readValue(resultJson, SearchMusicResponse.class);
        ArtistResponse artistResponse = searchMusicResponse.artists().getFirst();
        assertArtistResponse(artistResponse, artist);
        assertThat(searchMusicResponse.albums()).isEmpty();
    }

    @Test
    void shouldReturnSearchResultWithOnlyAlbumResponse_WhenArtistsNotFound() throws Exception{
        Artist artist = artistRepository.save(MusicFactoryIT.artist());
        Album album = albumRepository.save(MusicFactoryIT.album(artist));
        album.setImage(albumImageRepository.save(MusicFactoryIT.albumImage(album)));

        String fragment = "Black Holes";

        MvcResult result = mockMvc.perform(get("/search").param("fragment", fragment))
                .andExpect(status().isOk())
                .andReturn();

        String resultJson = result.getResponse().getContentAsString();
        SearchMusicResponse searchMusicResponse = objectMapper.readValue(resultJson, SearchMusicResponse.class);
        AlbumResponse albumResponse = searchMusicResponse.albums().getFirst();

        assertThat(searchMusicResponse.artists()).isEmpty();
        assertAlbumResponse(albumResponse, album);
    }

    @Test
    void shouldReturnApiErrorResponse_WhenFragmentIsEmpty() throws Exception{
        String fragment = " ";

        MvcResult result = mockMvc.perform(get("/search").param("fragment", fragment))
                .andExpect(status().isNoContent())
                .andReturn();

        String resultJson = result.getResponse().getContentAsString();
        ApiErrorResponse errorResponse = objectMapper.readValue(resultJson, ApiErrorResponse.class);
        assertApiErrorResponse(errorResponse);
    }

    @Test
    void shouldReturnApiErrorResponse_WhenFragmentIsNull() throws Exception{
        String fragment = null;

        MvcResult result = mockMvc.perform(get("/search").param("fragment", fragment))
                .andExpect(status().isNoContent())
                .andReturn();

        String resultJson = result.getResponse().getContentAsString();
        ApiErrorResponse errorResponse = objectMapper.readValue(resultJson, ApiErrorResponse.class);
        assertApiErrorResponse(errorResponse);
    }

    private void assertArtistResponse(ArtistResponse actualResponse, Artist expectedArtist){
        assertThat(actualResponse.id()).isEqualTo(expectedArtist.getId());
        assertThat(actualResponse.name()).isEqualTo(expectedArtist.getName());
    }

    private void assertAlbumResponse(AlbumResponse actualResponse, Album album){
        assertThat(actualResponse.getAlbumId()).isEqualTo(album.getId());
        assertThat(actualResponse.getTitle()).isEqualTo(album.getTitle());
        assertThat(actualResponse.getArtist().id()).isEqualTo(album.getArtist().getId());
        assertThat(actualResponse.getArtist().name()).isEqualTo(album.getArtist().getName());
        assertThat(actualResponse.getAlbumImage().getKey()).isEqualTo(album.getImage().getKey());
    }

    private void assertApiErrorResponse(ApiErrorResponse errorResponse){
        assertThat(errorResponse.code()).isEqualTo(ErrorType.INVALID_MUSIC_CONTENT.name());
        assertThat(errorResponse.status()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
