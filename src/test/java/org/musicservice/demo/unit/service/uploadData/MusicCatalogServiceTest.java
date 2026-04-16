package org.musicservice.demo.unit.service.uploadData;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.musicservice.demo.entity.genre.Genre;
import org.musicservice.demo.entity.image.AlbumImage;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.entity.music.Sound;
import org.musicservice.demo.integration.jamendo.response.MusicResponse;
import org.musicservice.demo.repository.image.AlbumImageRepository;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.musicservice.demo.repository.music.ArtistRepository;
import org.musicservice.demo.repository.music.SoundRepository;
import org.musicservice.demo.service.uploadData.MusicCatalogService;
import org.musicservice.demo.support.factory.unit.music.MusicDataFactory;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MusicCatalogServiceTest {

    @Mock
    private ArtistRepository artistRepository;
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private SoundRepository soundRepository;
    @Mock
    private AlbumImageRepository albumImageRepository;

    @InjectMocks
    private MusicCatalogService musicCatalogService;

    @Test
    void saveMusicData_ShouldCreateAllRecords(){
        Genre genre = new Genre();
        Artist artist = new Artist();
        Album album = new Album();
        AlbumImage albumImage = new AlbumImage();
        MusicResponse musicResponse = MusicDataFactory.musicResponse();

        when(soundRepository.existsByKey(musicResponse.mp3Key())).thenReturn(false);
        when(artistRepository.findByName(musicResponse.artist_name())).thenReturn(Optional.empty());
        when(artistRepository.save(any(Artist.class))).thenReturn(artist);
        when(albumRepository.findByTitle(musicResponse.album_name())).thenReturn(Optional.empty());
        when(albumRepository.save(any(Album.class))).thenReturn(album);
        when(albumImageRepository.save(any(AlbumImage.class))).thenReturn(albumImage);

        musicCatalogService.saveMusicData(musicResponse, genre);

        verify(soundRepository).existsByKey(musicResponse.mp3Key());
        verify(artistRepository).findByName(musicResponse.artist_name());
        verify(artistRepository).save(any(Artist.class));
        verify(albumRepository).findByTitle(musicResponse.album_name());
        verify(albumRepository).save(any(Album.class));
        verify(albumImageRepository).save(any(AlbumImage.class));
        verify(soundRepository).save(any(Sound.class));
    }

    @Test
    void saveMusicData_ShouldReturnEarly_WhenSoundIsExists(){
        Genre genre = new Genre();
        MusicResponse musicResponse = MusicDataFactory.musicResponse();

        when(soundRepository.existsByKey(musicResponse.mp3Key())).thenReturn(true);

        musicCatalogService.saveMusicData(musicResponse, genre);

        verifyNoInteractions(artistRepository);
        verifyNoInteractions(albumRepository);
        verifyNoInteractions(albumImageRepository);
        verifyNoMoreInteractions(soundRepository);
    }

}
