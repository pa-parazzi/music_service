package org.musicservice.demo.integration.controller.rest.likes;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.musicservice.demo.dto.likes.LikedSounds;
import org.musicservice.demo.entity.likes.SoundLike;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.entity.music.Sound;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.repository.likes.SoundLikeRepository;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.musicservice.demo.repository.music.ArtistRepository;
import org.musicservice.demo.repository.music.SoundRepository;
import org.musicservice.demo.repository.user.UserRepository;
import org.musicservice.demo.support.config.AbstractIntegrationTest;
import org.musicservice.demo.support.factory.it.music.MusicFactoryIT;
import org.musicservice.demo.support.factory.it.security.WithMockUserPrincipal;
import org.musicservice.demo.support.factory.it.user.UserDataFactoryIT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SoundLikeControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private AlbumRepository albumRepository;
    @Autowired
    private SoundRepository soundRepository;
    @Autowired
    private SoundLikeRepository soundLikeRepository;

    @BeforeEach
    void cleanupDb(){
        jdbcTemplate.execute("TRUNCATE TABLE users, artist, album, sound, sound_like RESTART IDENTITY CASCADE");
    }

    @Test
    @WithMockUserPrincipal
    void shouldReturnLikedSoundsIdsByOrderCreatedAtAndStatusIsOk_WhenUserHasLikedSounds() throws Exception{
        User user = userRepository.save(UserDataFactoryIT.userWithEnabledAccount(passwordEncoder));
        Artist artist = artistRepository.save(MusicFactoryIT.artist());
        Album album = albumRepository.save(MusicFactoryIT.album(artist));
        Sound sound = soundRepository.save(MusicFactoryIT.sound(artist, album));
        Sound sound2 = soundRepository.save(MusicFactoryIT.sound(artist, album));
        Sound sound3 = soundRepository.save(MusicFactoryIT.sound(artist, album));
        SoundLike soundLike = soundLikeRepository.save(MusicFactoryIT.soundLike(user, sound3));
        SoundLike soundLike2 = soundLikeRepository.save(MusicFactoryIT.soundLike(user, sound));
        SoundLike soundLike3 = soundLikeRepository.save(MusicFactoryIT.soundLike(user, sound2));
        List<Long> orderSoundIdsList = List.of(soundLike3.getSound().getId(), soundLike2.getSound().getId(), soundLike.getSound().getId());
        LikedSounds expectedLikedSoundIds = new LikedSounds(orderSoundIdsList);

        MvcResult result = mockMvc.perform(get("/api/liked-sounds/get"))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        LikedSounds actualLikedSoundIds = objectMapper.readValue(jsonResult, LikedSounds.class);
        assertThat(actualLikedSoundIds.likedSoundsIds()).containsExactlyElementsOf(expectedLikedSoundIds.likedSoundsIds());
    }

    @Test
    @WithMockUserPrincipal
    void shouldReturnIsEmptyStatusIsOk_WhenUserDoesNotLikedSounds() throws Exception{
        userRepository.save(UserDataFactoryIT.userWithEnabledAccount(passwordEncoder));

        MvcResult result = mockMvc.perform(get("/api/liked-sounds/get"))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        LikedSounds likedSoundsResult = objectMapper.readValue(jsonResult, LikedSounds.class);
        assertThat(likedSoundsResult.likedSoundsIds()).isEmpty();
    }

    @Test
    @WithMockUserPrincipal
    void shouldCreateSoundLikeAndReturnStatusIsCreated() throws Exception{
        User user = userRepository.save(UserDataFactoryIT.userWithEnabledAccount(passwordEncoder));
        Artist artist = artistRepository.save(MusicFactoryIT.artist());
        Album album = albumRepository.save(MusicFactoryIT.album(artist));
        Sound sound = soundRepository.save(MusicFactoryIT.sound(artist, album));

        mockMvc.perform(post("/api/liked-sounds/{soundId}", sound.getId()))
                .andExpect(status().isCreated());

        assertThat(soundLikeRepository.count()).isEqualTo(1);

        SoundLike soundLike = soundLikeRepository.findAll().getFirst();
        assertThat(soundLike.getSound().getId()).isEqualTo(sound.getId());
        assertThat(soundLike.getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    @WithMockUserPrincipal
    void shouldDeleteSoundLikeAndReturnStatusIsNoContent() throws Exception{
        User user = userRepository.save(UserDataFactoryIT.userWithEnabledAccount(passwordEncoder));
        Artist artist = artistRepository.save(MusicFactoryIT.artist());
        Album album = albumRepository.save(MusicFactoryIT.album(artist));
        Sound sound = soundRepository.save(MusicFactoryIT.sound(artist, album));
        SoundLike soundLike = soundLikeRepository.save(MusicFactoryIT.soundLike(user, sound));

        mockMvc.perform(delete("/api/liked-sounds/{soundId}", sound.getId()))
                .andExpect(status().isNoContent());

        assertThat(soundLikeRepository.findById(soundLike.getId())).isEmpty();
    }
}
