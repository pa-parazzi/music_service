package org.musicservice.demo.integration.controller.rest.like;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.musicservice.demo.dto.like.LikedAlbumId;
import org.musicservice.demo.dto.like.LikedAlbums;
import org.musicservice.demo.dto.like.UserGetLikesRequest;
import org.musicservice.demo.dto.like.UserLikedMusicRequest;
import org.musicservice.demo.entity.like.LikeAlbum;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.repository.like.LikeAlbumRepository;
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
public class LikeAlbumControllerIT extends AbstractIntegrationTest {

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
    private LikeAlbumRepository likeAlbumRepository;

    @BeforeEach
    void cleanupDb(){
        jdbcTemplate.execute("TRUNCATE TABLE users, artist, album, sound, like_album RESTART IDENTITY CASCADE");
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
        likeAlbumRepository.save(MusicFactoryIT.likeAlbum(user, album2));
        likeAlbumRepository.save(MusicFactoryIT.likeAlbum(user, album));
        likeAlbumRepository.save(MusicFactoryIT.likeAlbum(user, album3));

        List<Long> expectedOrderAlbumIds = likeAlbumRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId())
                .stream().map(likeAlbum -> likeAlbum.getAlbum().getId()).toList();

        MvcResult result = mockMvc.perform(post("/api/like_album/get")
                .contentType(MediaType.APPLICATION_JSON)
                .content(contentJson))
                .andExpect(status().isOk())
                .andReturn();

        String resultJson = result.getResponse().getContentAsString();
        LikedAlbums resultLikedAlbums = objectMapper.readValue(resultJson, LikedAlbums.class);
        List<Long> actualOrderAlbumIds = resultLikedAlbums.likedAlbumsIds().stream().map(LikedAlbumId::getAlbumId).toList();
        assertThat(actualOrderAlbumIds).containsExactlyElementsOf(expectedOrderAlbumIds);
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
    void shouldCreateLikeForAlbumAndReturnStatusIsAccept() throws Exception{
        User user = userRepository.save(UserDataFactoryIT.userWithEnabledAccount(passwordEncoder));
        Artist artist = artistRepository.save(MusicFactoryIT.artist());
        Album album = albumRepository.save(MusicFactoryIT.album(artist));
        UserLikedMusicRequest likeRequest = new UserLikedMusicRequest(user.getId(), album.getId());

        String jsonRequest = objectMapper.writeValueAsString(likeRequest);

        mockMvc.perform(post("/api/like_album/create")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());

        assertThat(likeAlbumRepository.count()).isEqualTo(1);

        LikeAlbum likeAlbum = likeAlbumRepository.findAll().getFirst();
        assertThat(likeAlbum.getAlbum().getId()).isEqualTo(album.getId());
        assertThat(likeAlbum.getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    void shouldSuccessDeleteLikeAlbumAndReturnStatusIsAccept() throws Exception{
        User user = userRepository.save(UserDataFactoryIT.userWithEnabledAccount(passwordEncoder));
        Artist artist = artistRepository.save(MusicFactoryIT.artist());
        Album album = albumRepository.save(MusicFactoryIT.album(artist));
        LikeAlbum likeAlbum = likeAlbumRepository.save(MusicFactoryIT.likeAlbum(user, album));
        UserLikedMusicRequest likeRequest = new UserLikedMusicRequest(user.getId(), album.getId());

        String jsonRequest = objectMapper.writeValueAsString(likeRequest);

        mockMvc.perform(delete("/api/like_album/delete")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());

        assertThat(likeAlbumRepository.findById(likeAlbum.getId())).isEmpty();
    }

}
