package org.musicservice.demo.integration.controller.rest.music;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.musicservice.demo.dto.like.LikedAlbumId;
import org.musicservice.demo.dto.like.LikedAlbums;
import org.musicservice.demo.dto.like.LikedSoundId;
import org.musicservice.demo.dto.like.LikedSounds;
import org.musicservice.demo.dto.music.album.AlbumResponse;
import org.musicservice.demo.dto.music.album.CollectionAlbumsResponse;
import org.musicservice.demo.dto.music.sound.TrackListResponse;
import org.musicservice.demo.dto.music.sound.SoundResponse;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.entity.music.Sound;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.exception.response.ApiErrorResponse;
import org.musicservice.demo.exception.response.ErrorType;
import org.musicservice.demo.mapper.like.LikeAlbumMapper;
import org.musicservice.demo.mapper.like.LikeSoundMapper;
import org.musicservice.demo.repository.image.AlbumImageRepository;
import org.musicservice.demo.repository.like.LikeAlbumRepository;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MusicCollectionControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private AlbumRepository albumRepository;
    @Autowired
    private AlbumImageRepository albumImageRepository;
    @Autowired
    private SoundRepository soundRepository;
    @Autowired
    private LikeSoundRepository likeSoundRepository;
    @Autowired
    private LikeAlbumRepository likeAlbumRepository;
    @Autowired
    private LikeSoundMapper likeSoundMapper;
    @Autowired
    private LikeAlbumMapper likeAlbumMapper;

    @BeforeEach
    void cleanupDb(){
        jdbcTemplate.execute("TRUNCATE TABLE users, artist, album, album_image, sound, like_sound, like_album RESTART IDENTITY CASCADE");
    }

    @Test
    void shouldReturnCollectionTracksOrderByCreatedAtAndStatusIsOk() throws Exception{
        User user = userRepository.save(UserDataFactoryIT.userWithoutIdAndEnabledAccount(passwordEncoder));
        Artist artist = artistRepository.save(MusicFactoryIT.artist());
        Album album = albumRepository.save(MusicFactoryIT.album(artist));
        List<Sound> soundList = soundRepository.saveAll(MusicFactoryIT.soundList(artist, album));
        likeSoundRepository.save(MusicFactoryIT.likeSound(user, soundList.get(2)));
        likeSoundRepository.save(MusicFactoryIT.likeSound(user, soundList.get(0)));
        likeSoundRepository.save(MusicFactoryIT.likeSound(user, soundList.get(1)));
        List<LikedSoundId> orderedLikedSoundIds = likeSoundRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId()).stream().map(likeSoundMapper::toResponse).toList();
        List<Long> orderedSoundsIds = orderedLikedSoundIds.stream().map(LikedSoundId::getSoundId).toList();
        LikedSounds likedSounds = new LikedSounds(orderedLikedSoundIds);
        String jsonContent = objectMapper.writeValueAsString(likedSounds);

        MvcResult result = mockMvc.perform(post("/collection/tracks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonContent))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        TrackListResponse trackListResponse = objectMapper.readValue(jsonResult, TrackListResponse.class);
        List<SoundResponse> soundListResponse = trackListResponse.soundList();
        List<Long> soundsIdsResponse = soundListResponse.stream().map(SoundResponse::getId).toList();
        assertThat(soundsIdsResponse).containsExactlyElementsOf(orderedSoundsIds);
    }

    @Test
    void shouldReturnCollectionAlbumsOrderByCreatedAtAndStatusIsOk() throws Exception{
        User user = userRepository.save(UserDataFactoryIT.userWithoutIdAndEnabledAccount(passwordEncoder));
        Artist artist = artistRepository.save(MusicFactoryIT.artist());
        List<Album> albumList = albumRepository.saveAll(MusicFactoryIT.albumList(artist));
        albumList.forEach(album -> album.setImage(albumImageRepository.save(MusicFactoryIT.albumImage(album))));
        likeAlbumRepository.save(MusicFactoryIT.likeAlbum(user, albumList.get(0)));
        likeAlbumRepository.save(MusicFactoryIT.likeAlbum(user, albumList.get(2)));
        likeAlbumRepository.save(MusicFactoryIT.likeAlbum(user, albumList.get(1)));
        List<LikedAlbumId> orderedLikedAlbumsIds = likeAlbumRepository.findAllByUserIdOrderByCreatedAtDesc(user.getId()).stream().map(likeAlbumMapper::toResponse).toList();
        List<Long> orderedAlbumsIds = orderedLikedAlbumsIds.stream().map(LikedAlbumId::getAlbumId).toList();
        LikedAlbums likedAlbums = new LikedAlbums(orderedLikedAlbumsIds);
        String contentJson = objectMapper.writeValueAsString(likedAlbums);

        MvcResult result = mockMvc.perform(post("/collection/albums")
                        .content(contentJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        CollectionAlbumsResponse collectionAlbumsResponse = objectMapper.readValue(jsonResult, CollectionAlbumsResponse.class);
        List<AlbumResponse> albums = collectionAlbumsResponse.albums();
        List<Long> actualAlbumsIds = albums.stream().map(AlbumResponse::getAlbumId).toList();
        assertThat(actualAlbumsIds).containsExactlyElementsOf(orderedAlbumsIds);
    }

    @Test
    void shouldReturnStatusIsNoContent_WhenLikedTracksIsEmpty() throws Exception{
        LikedSounds likedSounds = new LikedSounds(List.of());
        String contentJson = objectMapper.writeValueAsString(likedSounds);

        MvcResult result = mockMvc.perform(post("/collection/tracks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contentJson))
                .andExpect(status().isNoContent())
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        ApiErrorResponse errorResponse = objectMapper.readValue(jsonResult, ApiErrorResponse.class);
        assertThat(errorResponse.code()).isEqualTo(ErrorType.INVALID_MUSIC_CONTENT.name());
        assertThat(errorResponse.status()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void shouldReturnStatusIsNoContent_WhenLikedAlbumsIsEmpty() throws Exception{
        LikedAlbums likedAlbums = new LikedAlbums(List.of());
        String contentJson = objectMapper.writeValueAsString(likedAlbums);

        MvcResult result = mockMvc.perform(post("/collection/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contentJson))
                .andExpect(status().isNoContent())
                .andReturn();

        String jsonResult = result.getResponse().getContentAsString();
        ApiErrorResponse errorResponse = objectMapper.readValue(jsonResult, ApiErrorResponse.class);
        assertThat(errorResponse.code()).isEqualTo(ErrorType.INVALID_MUSIC_CONTENT.name());
        assertThat(errorResponse.status()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

}
