package org.musicservice.demo.integration.controller.admin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.musicservice.demo.dto.metadata.TrackMetadata;
import org.musicservice.demo.entity.genre.GenreName;
import org.musicservice.demo.entity.music.Sound;
import org.musicservice.demo.entity.user.Authority;
import org.musicservice.demo.integration.jamendo.JamendoClient;
import org.musicservice.demo.integration.jamendo.response.MusicResponse;
import org.musicservice.demo.repository.music.SoundRepository;
import org.musicservice.demo.service.uploadData.TrackMetadataWriter;
import org.musicservice.demo.support.config.AbstractSpringBootIT;
import org.musicservice.demo.support.factory.it.music.MusicFactoryIT;
import org.musicservice.demo.support.factory.it.security.WithMockUserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminControllerIT extends AbstractSpringBootIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private SoundRepository soundRepository;

    @MockitoBean
    private JamendoClient jamendoClient;
    @MockitoBean
    private TrackMetadataWriter trackMetadataWriter;

    @BeforeEach
    void cleanData(){
        jdbcTemplate.execute("TRUNCATE TABLE artist," +
                " album, album_image, sound RESTART IDENTITY CASCADE");
    }

    private final String genreName = GenreName.BLUES.name();
    private final int sizeApiContent = 10;

    @Test
    @WithMockUserPrincipal(authority = Authority.ADMIN)
    void shouldSuccessfulSaveAndWriteAllContent() throws Exception {
        List<MusicResponse> apiResponseList = MusicFactoryIT.musicResponseList(sizeApiContent);
        when(jamendoClient.tracksPack(genreName)).thenReturn(apiResponseList);

        RequestBuilder requestBuilder = post("/admin/import").param("genreName", genreName);
        mockMvc.perform(requestBuilder).andExpect(status().isAccepted());

        List<Sound> sounds = soundRepository.findAll();
        assertThat(sounds.size()).isEqualTo(apiResponseList.size());

        List<String> responseSoundsNames = apiResponseList.stream().map(MusicResponse::name).toList();
        List<String> soundsNames = sounds.stream().map(Sound::getTitle).toList();
        assertThat(responseSoundsNames).containsExactlyInAnyOrderElementsOf(soundsNames);

        verify(trackMetadataWriter, times(sizeApiContent)).write(any(TrackMetadata.class));
    }

    @Test
    @WithMockUserPrincipal(authority = Authority.ADMIN)
    void shouldReturnNotFoundStatus_WhenGenreNameIsInvalid() throws Exception {
        String genreName = "incorrect genre name";

        mockMvc.perform(post("/admin/import").param("genreName", genreName))
                .andExpect(status().isNotFound());

        assertThat(soundRepository.findAll()).isEmpty();
        verifyNoInteractions(trackMetadataWriter);
    }
}
