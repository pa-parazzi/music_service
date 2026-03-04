package org.musicservice.demo.integration.controller.rest.music;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.musicservice.demo.dto.likes.LikedContentIds;
import org.musicservice.demo.dto.music.album.AlbumsResponse;
import org.musicservice.demo.dto.music.album.AlbumResponse;
import org.musicservice.demo.dto.music.sound.SoundResponse;
import org.musicservice.demo.dto.music.sound.TracksResponse;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.entity.music.Sound;
import org.musicservice.demo.error.ApiErrorResponse;
import org.musicservice.demo.error.ErrorType;
import org.musicservice.demo.repository.image.AlbumImageRepository;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.musicservice.demo.repository.music.ArtistRepository;
import org.musicservice.demo.repository.music.SoundRepository;
import org.musicservice.demo.support.config.AbstractIntegrationTest;
import org.musicservice.demo.support.factory.it.music.MusicFactoryIT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MusicCollectionControllerIT extends AbstractIntegrationTest {

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
    private SoundRepository soundRepository;

    @BeforeEach
    void cleanupDb(){
        jdbcTemplate.execute("TRUNCATE TABLE users, artist, album, album_image, sound RESTART IDENTITY CASCADE");
    }

    @Test
    void shouldReturnCollectionTracksOrderByCreatedAtAndStatusIsOk() throws Exception{
        Artist artist = artistRepository.save(MusicFactoryIT.artist());
        Album album = albumRepository.save(MusicFactoryIT.album(artist));
        List<Sound> soundList = soundRepository.saveAll(MusicFactoryIT.soundList(artist, album));
        List<Long> orderedSoundIds = List.of(soundList.get(2).getId(), soundList.get(0).getId(), soundList.get(1).getId());
        LikedContentIds likedSounds = new LikedContentIds(orderedSoundIds);
        String jsonContent = objectMapper.writeValueAsString(likedSounds);

        MvcResult result = mockMvc.perform(post("/collection/tracks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        TracksResponse tracksResponse = objectMapper.readValue(jsonResult, TracksResponse.class);
        List<SoundResponse> soundListResponse = tracksResponse.soundList();
        List<Long> actualSoundsIds = soundListResponse.stream().map(SoundResponse::getId).toList();
        assertThat(actualSoundsIds).containsExactlyElementsOf(orderedSoundIds);
    }

    @Test
    void shouldReturnCollectionAlbumsOrderByCreatedAtAndStatusIsOk() throws Exception{
        Artist artist = artistRepository.save(MusicFactoryIT.artist());
        List<Album> albumList = albumRepository.saveAll(MusicFactoryIT.albumList(artist));
        albumList.forEach(album -> album.setImage(albumImageRepository.save(MusicFactoryIT.albumImage(album))));
        List<Long> orderedAlbumIds = List.of(albumList.get(2).getId(), albumList.get(0).getId(), albumList.get(1).getId());
        LikedContentIds likedContentIds = new LikedContentIds(orderedAlbumIds);
        String contentJson = objectMapper.writeValueAsString(likedContentIds);

        MvcResult result = mockMvc.perform(post("/collection/albums")
                        .content(contentJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        AlbumsResponse albumsResponse = objectMapper.readValue(jsonResult, AlbumsResponse.class);
        List<AlbumResponse> albums = albumsResponse.albums();
        List<Long> actualAlbumIds = albums.stream().map(AlbumResponse::getAlbumId).toList();
        assertThat(actualAlbumIds).containsExactlyElementsOf(orderedAlbumIds);
    }

    @Test
    void shouldReturnStatusIsNoContent_WhenLikedTracksIsEmpty() throws Exception{
        LikedContentIds likedSounds = new LikedContentIds(List.of());
        String contentJson = objectMapper.writeValueAsString(likedSounds);

        MvcResult result = mockMvc.perform(post("/collection/tracks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contentJson))
                .andExpect(status().isNoContent())
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        ApiErrorResponse errorResponse = objectMapper.readValue(jsonResult, ApiErrorResponse.class);
        assertApiErrorResponse(errorResponse);
    }

    @Test
    void shouldReturnStatusIsNoContent_WhenLikedAlbumsIsEmpty() throws Exception{
        LikedContentIds likedContentIds = new LikedContentIds(List.of());
        String contentJson = objectMapper.writeValueAsString(likedContentIds);

        MvcResult result = mockMvc.perform(post("/collection/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contentJson))
                .andExpect(status().isNoContent())
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        ApiErrorResponse errorResponse = objectMapper.readValue(jsonResult, ApiErrorResponse.class);
        assertApiErrorResponse(errorResponse);
    }

    private void assertApiErrorResponse(ApiErrorResponse errorResponse){
        assertThat(errorResponse.code()).isEqualTo(ErrorType.INVALID_MUSIC_CONTENT.name());
        assertThat(errorResponse.status()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

}
