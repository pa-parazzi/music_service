package org.musicservice.demo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.musicservice.demo.Authority.Authority;
import org.musicservice.demo.dto.music.request.UserLikesRequest;
import org.musicservice.demo.dto.music.response.LikeResponse;
import org.musicservice.demo.mapper.like.LikeResponseMapper;
import org.musicservice.demo.model.image.AlbumImage;
import org.musicservice.demo.model.music.Album;
import org.musicservice.demo.model.music.Artist;
import org.musicservice.demo.model.music.Like;
import org.musicservice.demo.model.music.Sound;
import org.musicservice.demo.model.user.User;
import org.musicservice.demo.repository.music.LikeRepository;
import org.musicservice.demo.service.music.LikeService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;
    @Mock
    private LikeResponseMapper likeResponseMapper;

    @InjectMocks
    private LikeService likeService;

    private User createFactoryUser(){
        User user = new User();
        user.setId(1L);
        user.setUsername("TestUser");
        user.setPassword("test123");
        user.setEmail("igor.bocharov.88@gmail.com");
        LocalDate date = LocalDate.of(2001, 1, 1);
        user.setDateOfBirth(date);
        user.setFailedLoginAttempts(0);
        user.setLockTime(null);
        user.setEnabled(false);
        user.setRole(Authority.USER);
        return user;
    }

    public List<Sound> createFactorySoundList(Artist artist, Album album){
        Long count = 0L;
        List<Sound> soundList = new ArrayList<>();
        Sound sound1 = new Sound("Track 1", 204);
        sound1.setId(count++);
        Sound sound2 = new Sound("Track 2", 189);
        sound2.setId(count++);
        Sound sound3 = new Sound("Track 3", 243);
        sound3.setId(count++);
        soundList.add(sound1);
        soundList.add(sound2);
        soundList.add(sound3);

        soundList.forEach(sound -> {
            sound.setArtist(artist);
            sound.setAlbum(album);
        });

        return soundList;
    }

    private Album createFactoryMusicData(){
        Artist artist = new Artist("Muse");
        artist.setId(1L);
        Album album = new Album("Black Holes and Revelations");
        album.setId(1L);
        String s3ImgKey = album.getTitle() + ".jpg";
        AlbumImage albumImage = new AlbumImage(s3ImgKey, album);
        albumImage.setId(1L);
        List<Sound> soundList = createFactorySoundList(artist, album);

        artist.setAlbums(Collections.singletonList(album));
        artist.setSoundList(soundList);

        album.setImage(albumImage);
        album.setArtist(artist);
        album.setSoundList(soundList);

        soundList.forEach(sound -> {
            String s3TrackKey = String.format("%s/", sound.getAlbum().getTitle() + sound.getTitle() + ".mp3");
            sound.setKey(s3TrackKey);
        });

        return album;
    }

    private Like createFactoryLike(Album album, User user){
        Like like = new Like();
        like.setUser(user);
        like.setTarget(album);
        return like;
    }

    @Test
    void findAllByUserRequest_ReturnLikeResponses(){
        User user = createFactoryUser();
        Album album = createFactoryMusicData();
        Like like = createFactoryLike(album, user);
        Long userId = user.getId();

        UserLikesRequest userRequest = new UserLikesRequest();
        userRequest.setUserId(userId);

        LikeResponse response = new LikeResponse();
        response.setTargetType("album");
        response.setTargetId(album.getId());

        when(likeRepository.findAllByUserId(userId)).thenReturn(Collections.singletonList(like));
        when(likeResponseMapper.toResponse(like)).thenReturn(response);

        List<LikeResponse> actualResponse = likeService.findAllByUserRequest(userRequest);

        // Проверка значений из ответа что вернул сервис
        assertThat(actualResponse).hasSize(1).first().satisfies(res -> {
            assertThat(res.getTargetType()).isEqualTo("album");
            assertThat(res.getTargetId()).isEqualTo(album.getId());
        });

        // Проверка на наличие вызова метода у репозитория, и вызов маппера количество раз равным размеру списка ответа
        verify(likeRepository).findAllByUserId(userId);
        verify(likeResponseMapper, times(actualResponse.size())).toResponse(like);
    }

}
