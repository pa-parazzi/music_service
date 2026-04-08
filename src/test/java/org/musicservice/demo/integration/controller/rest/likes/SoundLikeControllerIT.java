package org.musicservice.demo.integration.controller.rest.likes;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.musicservice.demo.dto.likes.LikeStatusResponse;
import org.musicservice.demo.entity.likes.SoundLike;
import org.musicservice.demo.entity.music.Sound;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.repository.likes.SoundLikeRepository;
import org.musicservice.demo.repository.user.UserRepository;
import org.musicservice.demo.support.config.AbstractIntegrationTest;
import org.musicservice.demo.support.factory.it.music.MusicFactoryIT;
import org.musicservice.demo.support.factory.it.security.WithMockUserPrincipal;
import org.musicservice.demo.support.factory.it.user.UserDataFactoryIT;
import org.musicservice.demo.support.fixture.SoundTestFixture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(SoundTestFixture.class)
public class SoundLikeControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SoundTestFixture soundFixture;
    @Autowired
    private SoundLikeRepository soundLikeRepository;

    @BeforeEach
    void cleanupDb(){
        jdbcTemplate.execute("TRUNCATE TABLE users, genre, artist, album, sound, sound_like RESTART IDENTITY CASCADE");
    }

    @Test
    @WithMockUserPrincipal
    void shouldReturnsLikeStatusIsTrueAndHttpStatusIsOk_WhenSoundLikeExists() throws Exception{
        User user = userRepository.save(UserDataFactoryIT.userWithEnabledAccount());
        Sound sound = soundFixture.soundAggregateWithOneSound().sounds().getFirst();
        soundLikeRepository.save(MusicFactoryIT.soundLike(user, sound));

        MvcResult result = mockMvc.perform(get("/api/sound-like/is-liked/{id}", sound.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likeStatus").exists())
                .andReturn();

        String resultJson = result.getResponse().getContentAsString();
        LikeStatusResponse likeStatusResponse = objectMapper.readValue(resultJson, LikeStatusResponse.class);
        assertThat(likeStatusResponse.likeStatus()).isTrue();
    }

    @Test
    @WithMockUserPrincipal
    void shouldReturnsLikeStatusIsFalseAndHttpStatusIsOk_WhenSoundLikeIsNotExists() throws Exception{
        MvcResult result = mockMvc.perform(get("/api/sound-like/is-liked/{id}", 256L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likeStatus").exists())
                .andReturn();

        String resultJson = result.getResponse().getContentAsString();
        LikeStatusResponse likeStatusResponse = objectMapper.readValue(resultJson, LikeStatusResponse.class);
        assertThat(likeStatusResponse.likeStatus()).isFalse();
    }

    @Test
    @WithMockUserPrincipal
    void shouldCreateSoundLikeAndReturnStatusIsCreated() throws Exception{
        User user = userRepository.save(UserDataFactoryIT.userWithEnabledAccount());
        Sound sound = soundFixture.soundAggregateWithOneSound().sounds().getFirst();

        mockMvc.perform(post("/api/sound-like/{id}", sound.getId()))
                .andExpect(status().isCreated());

        assertThat(soundLikeRepository.count()).isEqualTo(1);

        SoundLike soundLike = soundLikeRepository.findAll().getFirst();
        assertThat(soundLike.getSound().getId()).isEqualTo(sound.getId());
        assertThat(soundLike.getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    @WithMockUserPrincipal
    void shouldDeleteSoundLikeAndReturnStatusIsNoContent() throws Exception{
        User user = userRepository.save(UserDataFactoryIT.userWithEnabledAccount());
        Sound sound = soundFixture.soundAggregateWithOneSound().sounds().getFirst();
        SoundLike soundLike = soundLikeRepository.save(MusicFactoryIT.soundLike(user, sound));

        mockMvc.perform(delete("/api/sound-like/{id}", sound.getId()))
                .andExpect(status().isNoContent());

        assertThat(soundLikeRepository.findById(soundLike.getId())).isEmpty();
    }
}
