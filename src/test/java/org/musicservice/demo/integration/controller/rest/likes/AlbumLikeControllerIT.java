package org.musicservice.demo.integration.controller.rest.likes;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.musicservice.demo.dto.likes.LikedContentIds;
import org.musicservice.demo.entity.likes.AlbumLike;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.repository.likes.AlbumLikeRepository;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.musicservice.demo.repository.music.ArtistRepository;
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
public class AlbumLikeControllerIT extends AbstractIntegrationTest {

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
    private AlbumLikeRepository albumLikeRepository;

    @BeforeEach
    void cleanupDb(){
        jdbcTemplate.execute("TRUNCATE TABLE users, artist, album, sound, album_like RESTART IDENTITY CASCADE");
    }

    @Test
    @WithMockUserPrincipal
    void shouldReturnLikedAlbumIdsByOrderCreatedAtAndStatusIsOk_WhenUserHasLikedAlbum() throws Exception{
        User user = userRepository.save(UserDataFactoryIT.userWithEnabledAccount(passwordEncoder));
        Artist artist = artistRepository.save(MusicFactoryIT.artist());
        Album album = albumRepository.save(MusicFactoryIT.album(artist));
        Album album2 = albumRepository.save(MusicFactoryIT.album2(artist));
        Album album3 = albumRepository.save(MusicFactoryIT.album3(artist));
        AlbumLike albumLike = albumLikeRepository.save(MusicFactoryIT.albumLike(user, album2));
        AlbumLike albumLike2 = albumLikeRepository.save(MusicFactoryIT.albumLike(user, album));
        AlbumLike albumLike3 = albumLikeRepository.save(MusicFactoryIT.albumLike(user, album3));
        List<Long> orderAlbumIdsList = List.of(albumLike3.getAlbum().getId(), albumLike2.getAlbum().getId(), albumLike.getAlbum().getId());
        LikedContentIds expectedOrderAlbumIds = new LikedContentIds(orderAlbumIdsList);

        MvcResult result = mockMvc.perform(get("/api/liked-albums/get"))
                .andExpect(status().isOk())
                .andReturn();

        String resultJson = result.getResponse().getContentAsString();
        LikedContentIds actualOrderAlbumIds = objectMapper.readValue(resultJson, LikedContentIds.class);
        assertThat(actualOrderAlbumIds.ids()).containsExactlyElementsOf(expectedOrderAlbumIds.ids());
    }

    @Test
    @WithMockUserPrincipal
    void shouldReturnStatusIsOkAndResultIsEmpty_WhenUserDoesNotHaveLikedAlbums() throws Exception{
        userRepository.save(UserDataFactoryIT.userWithEnabledAccount(passwordEncoder));

        MvcResult result = mockMvc.perform(get("/api/liked-albums/get"))
                .andExpect(status().isOk())
                .andReturn();

        String resultJson = result.getResponse().getContentAsString();
        LikedContentIds resultLikedContentIds = objectMapper.readValue(resultJson, LikedContentIds.class);
        assertThat(resultLikedContentIds.ids()).isEmpty();
    }

    @Test
    @WithMockUserPrincipal
    void shouldCreateAlbumLikeAndReturnStatusIsCreated() throws Exception{
        User user = userRepository.save(UserDataFactoryIT.userWithEnabledAccount(passwordEncoder));
        Artist artist = artistRepository.save(MusicFactoryIT.artist());
        Album album = albumRepository.save(MusicFactoryIT.album(artist));

        mockMvc.perform(post("/api/liked-albums/{albumId}", album.getId()))
                .andExpect(status().isCreated());

        assertThat(albumLikeRepository.count()).isEqualTo(1);

        AlbumLike albumLike = albumLikeRepository.findAll().getFirst();
        assertThat(albumLike.getAlbum().getId()).isEqualTo(album.getId());
        assertThat(albumLike.getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    @WithMockUserPrincipal
    void shouldSuccessDeleteAlbumLikeAndReturnStatusIsNoContent() throws Exception{
        User user = userRepository.save(UserDataFactoryIT.userWithEnabledAccount(passwordEncoder));
        Artist artist = artistRepository.save(MusicFactoryIT.artist());
        Album album = albumRepository.save(MusicFactoryIT.album(artist));
        AlbumLike albumLike = albumLikeRepository.save(MusicFactoryIT.albumLike(user, album));

        mockMvc.perform(delete("/api/liked-albums/{albumId}", album.getId()))
                .andExpect(status().isNoContent());

        assertThat(albumLikeRepository.findById(albumLike.getId())).isEmpty();
    }

}
