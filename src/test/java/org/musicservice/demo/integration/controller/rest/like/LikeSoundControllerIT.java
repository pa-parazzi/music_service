package org.musicservice.demo.integration.controller.rest.like;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.musicservice.demo.dto.like.LikedSoundId;
import org.musicservice.demo.dto.like.LikedSounds;
import org.musicservice.demo.dto.like.UserGetLikesRequest;
import org.musicservice.demo.dto.like.UserLikeRequest;
import org.musicservice.demo.entity.like.LikeSound;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.entity.music.Sound;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.repository.like.LikeSoundRepository;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.musicservice.demo.repository.music.ArtistRepository;
import org.musicservice.demo.repository.music.SoundRepository;
import org.musicservice.demo.repository.user.UserRepository;
import org.musicservice.demo.support.config.AbstractIntegrationTest;
import org.musicservice.demo.support.factory.it.music.MusicFactoryIT;
import org.musicservice.demo.support.factory.it.user.UserDataFactoryIT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LikeSoundControllerIT extends AbstractIntegrationTest {

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
    private LikeSoundRepository likeSoundRepository;

    @BeforeEach
    void cleanupDb(){
        jdbcTemplate.execute("TRUNCATE TABLE users, artist, album, sound, like_sound RESTART IDENTITY CASCADE");
    }

    @Test
    void shouldReturnLikedSoundsIdsByOrderCreatedAtAndStatusIsOk_WhenUserHasLikedSounds() throws Exception{
        User user = userRepository.save(UserDataFactoryIT.userWithoutIdAndEnabledAccount(passwordEncoder));
        UserGetLikesRequest userRequest = new UserGetLikesRequest(user.getId());
        String contentJson = objectMapper.writeValueAsString(userRequest);

        Artist artist = artistRepository.save(MusicFactoryIT.artist());
        Album album = albumRepository.save(MusicFactoryIT.album(artist));
        List<Sound> soundList = soundRepository.saveAll(MusicFactoryIT.soundList(artist, album));
        for (Sound sound : soundList) {
            likeSoundRepository.save(MusicFactoryIT.likeSound(user, sound));
        }

        List<Long> expectedOrderSoundIds = likeSoundRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId())
                .stream().map(likeSound -> likeSound.getSound().getId()).toList();

        MvcResult result = mockMvc.perform(post("/sound/like/get")
                .contentType(MediaType.APPLICATION_JSON)
                .content(contentJson))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        LikedSounds likedSoundsResult = objectMapper.readValue(jsonResult, LikedSounds.class);
        List<Long> actualOrderSoundsIds = likedSoundsResult.likedSoundsIds().stream().map(LikedSoundId::getSoundId).toList();
        assertThat(actualOrderSoundsIds).containsExactlyElementsOf(expectedOrderSoundIds);
    }

    @Test
    void shouldReturnIsEmptyStatusIsOk_WhenUserDoesNotLikedSounds() throws Exception{
        User user = userRepository.save(UserDataFactoryIT.userWithoutIdAndEnabledAccount(passwordEncoder));
        UserGetLikesRequest userRequest = new UserGetLikesRequest(user.getId());
        String contentJson = objectMapper.writeValueAsString(userRequest);

        Artist artist = artistRepository.save(MusicFactoryIT.artist());
        Album album = albumRepository.save(MusicFactoryIT.album(artist));
        soundRepository.saveAll(MusicFactoryIT.soundList(artist, album));

        MvcResult result = mockMvc.perform(post("/sound/like/get")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contentJson))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        LikedSounds likedSoundsResult = objectMapper.readValue(jsonResult, LikedSounds.class);
        assertThat(likedSoundsResult.likedSoundsIds()).isEmpty();
    }

    @Test
    void shouldCreateLikeSoundAndReturnStatusIsAccept() throws Exception{
        User user = userRepository.save(UserDataFactoryIT.userWithoutIdAndEnabledAccount(passwordEncoder));
        Artist artist = artistRepository.save(MusicFactoryIT.artist());
        Album album = albumRepository.save(MusicFactoryIT.album(artist));
        List<Sound> soundList = soundRepository.saveAll(MusicFactoryIT.soundList(artist, album));
        Sound likedSound = soundList.getFirst();

        UserLikeRequest userRequest = new UserLikeRequest(user.getId(), likedSound.getId());
        String contentJson = objectMapper.writeValueAsString(userRequest);

        mockMvc.perform(post("/sound/like/create")
                .content(contentJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());

        assertThat(likeSoundRepository.count()).isEqualTo(1);

        LikeSound likeSound = likeSoundRepository.findAll().getFirst();
        assertThat(likeSound.getSound().getId()).isEqualTo(likedSound.getId());
        assertThat(likeSound.getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    void shouldDeleteLikeSoundAndReturnStatusIsAccept() throws Exception{
        User user = userRepository.save(UserDataFactoryIT.userWithoutIdAndEnabledAccount(passwordEncoder));
        Artist artist = artistRepository.save(MusicFactoryIT.artist());
        Album album = albumRepository.save(MusicFactoryIT.album(artist));
        List<Sound> soundList = soundRepository.saveAll(MusicFactoryIT.soundList(artist, album));
        Sound likedSound = soundList.get(1);
        LikeSound likeSound = likeSoundRepository.save(MusicFactoryIT.likeSound(user, likedSound));

        UserLikeRequest userRequest = new UserLikeRequest(user.getId(), likedSound.getId());
        String contentJson = objectMapper.writeValueAsString(userRequest);

        mockMvc.perform(delete("/sound/like/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contentJson))
                .andExpect(status().isAccepted());

        assertThat(likeSoundRepository.findById(likeSound.getId())).isEmpty();
    }
}
