package org.musicservice.demo.integration.controller.rest.music;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.musicservice.demo.dto.music.sound.SoundResponse;
import org.musicservice.demo.dto.music.sound.TrackListResponse;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.entity.music.Sound;
import org.musicservice.demo.exception.response.ApiErrorResponse;
import org.musicservice.demo.exception.response.ErrorType;
import org.musicservice.demo.mapper.music.SoundMapper;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.musicservice.demo.repository.music.ArtistRepository;
import org.musicservice.demo.repository.music.SoundRepository;
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
public class SoundControllerIT extends AbstractIntegrationTest {

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
    private SoundRepository soundRepository;
    @Autowired
    private SoundMapper soundMapper;

    @BeforeEach
    void cleanupDb(){
        jdbcTemplate.execute("TRUNCATE TABLE artist, album, sound RESTART IDENTITY CASCADE");
    }

    @Test
    void shouldReturnValidTrackListByAlbumIdAndStatusIsOk() throws Exception{
        Artist artist = artistRepository.save(MusicFactoryIT.artist());
        Album album = albumRepository.save(MusicFactoryIT.album(artist));
        List<Sound> soundList = soundRepository.saveAll(MusicFactoryIT.soundListByAlbum(artist, album));
        List<SoundResponse> expectedSoundList = soundList.stream().map(soundMapper::toResponse).toList();
        TrackListResponse expectedResponse = new TrackListResponse(expectedSoundList);

        MvcResult result = mockMvc.perform(get("/api/sound/album/{id}", album.getId()))
                .andExpect(status().isOk())
                .andReturn();

        String resultJson = result.getResponse().getContentAsString();
        TrackListResponse actualResponse = objectMapper.readValue(resultJson, TrackListResponse.class);
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    void shouldReturnValidTrackListByArtistIdAndStatusIsOk() throws Exception {
        Artist artist = artistRepository.save(MusicFactoryIT.artist());
        Album album = albumRepository.save(MusicFactoryIT.album(artist));
        List<Sound> soundList = soundRepository.saveAll(MusicFactoryIT.soundListByArtist(artist, album));
        List<SoundResponse> expectedSoundList = soundList.stream().map(soundMapper::toResponse).toList();
        TrackListResponse expectedResponse = new TrackListResponse(expectedSoundList);

        MvcResult result = mockMvc.perform(get("/api/sound/artist/{id}", artist.getId()))
                .andExpect(status().isOk())
                .andReturn();

        String resultJson = result.getResponse().getContentAsString();
        TrackListResponse actualResponse = objectMapper.readValue(resultJson, TrackListResponse.class);
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    void shouldReturnStatusIsNotFound_WhenAlbumIsNotExistsById() throws Exception{
        Artist artist = artistRepository.save(MusicFactoryIT.artist());
        Album album = albumRepository.save(MusicFactoryIT.album(artist));
        soundRepository.saveAll(MusicFactoryIT.soundListByAlbum(artist, album));

        MvcResult result = mockMvc.perform(get("/api/sound/album/{id}", 238))
                .andExpect(status().isNotFound())
                .andReturn();

        String resultJson = result.getResponse().getContentAsString();
        ApiErrorResponse errorResponse = objectMapper.readValue(resultJson, ApiErrorResponse.class);
        assertThatApiErrorContractHttpStatusIsNotFound(errorResponse);
    }

    @Test
    void shouldReturnStatusIsNotFound_WhenArtistIsNotExistsById() throws Exception {
        Artist artist = artistRepository.save(MusicFactoryIT.artist());
        Album album = albumRepository.save(MusicFactoryIT.album(artist));
        soundRepository.saveAll(MusicFactoryIT.soundListByArtist(artist, album));

        MvcResult result = mockMvc.perform(get("/api/sound/artist/{id}", 254))
                .andExpect(status().isNotFound())
                .andReturn();

        String resultJson = result.getResponse().getContentAsString();
        ApiErrorResponse errorResponse = objectMapper.readValue(resultJson, ApiErrorResponse.class);
        assertThatApiErrorContractHttpStatusIsNotFound(errorResponse);
    }

    private void assertThatApiErrorContractHttpStatusIsNotFound(ApiErrorResponse errorResponse){
        assertThat(errorResponse.code()).isEqualTo(ErrorType.API_ERROR.name());
        assertThat(errorResponse.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }
}
