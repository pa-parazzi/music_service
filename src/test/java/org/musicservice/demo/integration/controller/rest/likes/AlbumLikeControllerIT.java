package org.musicservice.demo.integration.controller.rest.likes;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.musicservice.demo.dto.likes.LikeStatusResponse;
import org.musicservice.demo.entity.genre.Genre;
import org.musicservice.demo.entity.genre.GenreName;
import org.musicservice.demo.entity.likes.AlbumLike;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.repository.likes.AlbumLikeRepository;
import org.musicservice.demo.repository.music.GenreRepository;
import org.musicservice.demo.repository.user.UserRepository;
import org.musicservice.demo.support.config.AbstractSpringBootIT;
import org.musicservice.demo.support.factory.it.music.MusicFactoryIT;
import org.musicservice.demo.support.factory.it.security.WithMockUserPrincipal;
import org.musicservice.demo.support.factory.it.user.UserDataFactoryIT;
import org.musicservice.demo.support.fixture.integration.AlbumTestFixture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(AlbumTestFixture.class)
public class AlbumLikeControllerIT extends AbstractSpringBootIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AlbumTestFixture albumFixture;
    @Autowired
    private AlbumLikeRepository albumLikeRepository;
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
    void shouldReturnsLikeStatusIsTrue_WhenAlbumLikeExists() throws Exception {
        Album album = albumFixture.albumAggregateWithOneAlbum(genre).albums().getFirst();
        albumLikeRepository.save(MusicFactoryIT.albumLike(user, album));

        MvcResult result = mockMvc.perform(get("/api/private/album-like/is-liked/{id}", album.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likeStatus").exists())
                .andReturn();

        String resultJson = result.getResponse().getContentAsString();
        LikeStatusResponse likeStatusResponse = objectMapper.readValue(resultJson, LikeStatusResponse.class);
        assertThat(likeStatusResponse.likeStatus()).isTrue();
    }

    @Test
    @WithMockUserPrincipal
    void shouldReturnsLikeStatusIsFalse_WhenAlbumLikeIsNotExists() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/private/album-like/is-liked/{id}", 256L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likeStatus").exists())
                .andReturn();

        String resultJson = result.getResponse().getContentAsString();
        LikeStatusResponse likeStatusResponse = objectMapper.readValue(resultJson, LikeStatusResponse.class);
        assertThat(likeStatusResponse.likeStatus()).isFalse();
    }

    @Test
    @WithMockUserPrincipal
    void shouldCreateAlbumLike() throws Exception{
        Album album = albumFixture.albumAggregateWithOneAlbum(genre).albums().getFirst();

        mockMvc.perform(post("/api/private/album-like/{id}", album.getId()))
                .andExpect(status().isCreated());

        assertThat(albumLikeRepository.count()).isEqualTo(1);

        AlbumLike albumLike = albumLikeRepository.findAll().getFirst();
        assertThat(albumLike.getAlbum().getId()).isEqualTo(album.getId());
        assertThat(albumLike.getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    @WithMockUserPrincipal
    void shouldSuccessDeleteAlbumLike() throws Exception{
        Album album = albumFixture.albumAggregateWithOneAlbum(genre).albums().getFirst();
        AlbumLike albumLike = albumLikeRepository.save(MusicFactoryIT.albumLike(user, album));

        mockMvc.perform(delete("/api/private/album-like/{id}", album.getId()))
                .andExpect(status().isNoContent());

        assertThat(albumLikeRepository.findById(albumLike.getId())).isEmpty();
    }

}
