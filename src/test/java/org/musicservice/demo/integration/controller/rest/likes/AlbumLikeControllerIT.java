package org.musicservice.demo.integration.controller.rest.likes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.musicservice.demo.dto.likes.LikedAlbums;
import org.musicservice.demo.dto.likes.UserGetLikesRequest;
import org.musicservice.demo.dto.likes.UserLikedMusicRequest;
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
import org.musicservice.demo.support.factory.it.user.UserDataFactoryIT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    void shouldReturnLikedAlbumIdsByOrderCreatedAtAndStatusIsOk_WhenUserHasLikedAlbum() throws Exception{
        User user = userRepository.save(UserDataFactoryIT.userWithEnabledAccount(passwordEncoder));
        UserGetLikesRequest userRequest = new UserGetLikesRequest(user.getId());
        String contentJson = objectMapper.writeValueAsString(userRequest);

        Artist artist = artistRepository.save(MusicFactoryIT.artist());
        Album album = albumRepository.save(MusicFactoryIT.album(artist));
        Album album2 = albumRepository.save(MusicFactoryIT.album2(artist));
        Album album3 = albumRepository.save(MusicFactoryIT.album3(artist));
        AlbumLike albumLike = albumLikeRepository.save(MusicFactoryIT.albumLike(user, album2));
        AlbumLike albumLike2 = albumLikeRepository.save(MusicFactoryIT.albumLike(user, album));
        AlbumLike albumLike3 = albumLikeRepository.save(MusicFactoryIT.albumLike(user, album3));
        List<Long> orderAlbumIdsList = List.of(albumLike3.getAlbum().getId(), albumLike2.getAlbum().getId(), albumLike.getAlbum().getId());
        LikedAlbums expectedOrderAlbumIds = new LikedAlbums(orderAlbumIdsList);

        MvcResult result = mockMvc.perform(post("/api/like_album/get")
                .contentType(MediaType.APPLICATION_JSON)
                .content(contentJson))
                .andExpect(status().isOk())
                .andReturn();

        String resultJson = result.getResponse().getContentAsString();
        LikedAlbums actualOrderAlbumIds = objectMapper.readValue(resultJson, LikedAlbums.class);
        assertThat(actualOrderAlbumIds.likedAlbumsIds()).containsExactlyElementsOf(expectedOrderAlbumIds.likedAlbumsIds());
    }

    @Test
    void shouldReturnStatusIsOkAndResultIsEmpty_WhenUserDoesNotHaveLikedAlbums() throws Exception{
        User user = userRepository.save(UserDataFactoryIT.userWithEnabledAccount(passwordEncoder));
        UserGetLikesRequest userRequest = new UserGetLikesRequest(user.getId());
        String contentJson = objectMapper.writeValueAsString(userRequest);

        Artist artist = artistRepository.save(MusicFactoryIT.artist());
        albumRepository.save(MusicFactoryIT.album(artist));

        MvcResult result = mockMvc.perform(post("/api/like_album/get")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contentJson))
                .andExpect(status().isOk())
                .andReturn();

        String resultJson = result.getResponse().getContentAsString();
        LikedAlbums resultLikedAlbums = objectMapper.readValue(resultJson, LikedAlbums.class);
        assertThat(resultLikedAlbums.likedAlbumsIds()).isEmpty();
    }

    @Test
    void shouldCreateAlbumLikeAndReturnStatusIsAccept() throws Exception{
        User user = userRepository.save(UserDataFactoryIT.userWithEnabledAccount(passwordEncoder));
        Artist artist = artistRepository.save(MusicFactoryIT.artist());
        Album album = albumRepository.save(MusicFactoryIT.album(artist));
        UserLikedMusicRequest likeRequest = new UserLikedMusicRequest(user.getId(), album.getId());

        String jsonRequest = objectMapper.writeValueAsString(likeRequest);

        mockMvc.perform(post("/api/like_album/create")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());

        assertThat(albumLikeRepository.count()).isEqualTo(1);

        AlbumLike albumLike = albumLikeRepository.findAll().getFirst();
        assertThat(albumLike.getAlbum().getId()).isEqualTo(album.getId());
        assertThat(albumLike.getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    void shouldSuccessDeleteAlbumLikeAndReturnStatusIsAccept() throws Exception{
        User user = userRepository.save(UserDataFactoryIT.userWithEnabledAccount(passwordEncoder));
        Artist artist = artistRepository.save(MusicFactoryIT.artist());
        Album album = albumRepository.save(MusicFactoryIT.album(artist));
        AlbumLike albumLike = albumLikeRepository.save(MusicFactoryIT.albumLike(user, album));
        UserLikedMusicRequest likeRequest = new UserLikedMusicRequest(user.getId(), album.getId());

        String jsonRequest = objectMapper.writeValueAsString(likeRequest);

        mockMvc.perform(delete("/api/like_album/delete")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());

        assertThat(albumLikeRepository.findById(albumLike.getId())).isEmpty();
    }

}
