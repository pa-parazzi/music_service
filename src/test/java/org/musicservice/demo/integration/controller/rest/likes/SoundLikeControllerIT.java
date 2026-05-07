package org.musicservice.demo.integration.controller.rest.likes;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.musicservice.demo.dto.likes.LikeStatusResponse;
import org.musicservice.demo.entity.genre.Genre;
import org.musicservice.demo.entity.genre.GenreName;
import org.musicservice.demo.entity.likes.SoundLike;
import org.musicservice.demo.entity.music.Sound;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.repository.likes.SoundLikeRepository;
import org.musicservice.demo.repository.music.GenreRepository;
import org.musicservice.demo.repository.user.UserRepository;
import org.musicservice.demo.support.config.AbstractSpringBootIT;
import org.musicservice.demo.support.factory.it.music.MusicFactoryIT;
import org.musicservice.demo.support.factory.it.security.WithMockUserPrincipal;
import org.musicservice.demo.support.factory.it.user.UserDataFactoryIT;
import org.musicservice.demo.support.fixture.integration.SoundTestFixture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SoundTestFixture.class)
public class SoundLikeControllerIT extends AbstractSpringBootIT {

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
    @Autowired
    private GenreRepository genreRepository;

    private Genre genre;
    private User user;

    @BeforeEach
    void setup(){
        truncateTables();
        this.user = userRepository.save(UserDataFactoryIT.userWithEnabledAccount());
    }

    @BeforeAll
    void getGenre(){
        genre = genreRepository.findByName(GenreName.ROCK).orElseThrow();
    }

    private void truncateTables(){
        jdbcTemplate.execute("TRUNCATE TABLE users, artist, album, album_image," +
                " sound, sound_like, album_like RESTART IDENTITY CASCADE");
    }

    @Test
    @WithMockUserPrincipal
    void shouldReturnsLikeStatusIsTrueAndHttpStatusIsOk_WhenSoundLikeExists() throws Exception{
        Sound sound = soundFixture.soundAggregateWithOneSound(genre).sounds().getFirst();
        soundLikeRepository.save(MusicFactoryIT.soundLike(user, sound));

        MvcResult result = mockMvc.perform(get("/api/private/sound-like/is-liked/{id}", sound.getId()))
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
        MvcResult result = mockMvc.perform(get("/api/private/sound-like/is-liked/{id}", 256L))
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
        Sound sound = soundFixture.soundAggregateWithOneSound(genre).sounds().getFirst();

        mockMvc.perform(post("/api/private/sound-like/{id}", sound.getId()))
                .andExpect(status().isCreated());

        assertThat(soundLikeRepository.count()).isEqualTo(1);

        SoundLike soundLike = soundLikeRepository.findAll().getFirst();
        assertThat(soundLike.getSound().getId()).isEqualTo(sound.getId());
        assertThat(soundLike.getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    @WithMockUserPrincipal
    void shouldDeleteSoundLikeAndReturnStatusIsNoContent() throws Exception{
        Sound sound = soundFixture.soundAggregateWithOneSound(genre).sounds().getFirst();
        SoundLike soundLike = soundLikeRepository.save(MusicFactoryIT.soundLike(user, sound));

        mockMvc.perform(delete("/api/private/sound-like/{id}", sound.getId()))
                .andExpect(status().isNoContent());

        assertThat(soundLikeRepository.findById(soundLike.getId())).isEmpty();
    }
}
