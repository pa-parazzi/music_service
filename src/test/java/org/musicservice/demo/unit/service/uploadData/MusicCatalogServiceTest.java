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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    void saveMusicData_ShouldReturnTrueAndSaveAllRecords_WhenSoundIsNotExists(){
        Genre genre = new Genre();
        MusicResponse musicResponse = MusicDataFactory.musicResponse();

        when(soundRepository.existsByTitle(musicResponse.name())).thenReturn(false);
        when(artistRepository.findByName(musicResponse.artist_name())).thenReturn(Optional.empty());
        when(artistRepository.save(any(Artist.class))).thenReturn(new Artist());
        when(albumRepository.findByTitle(musicResponse.album_name())).thenReturn(Optional.empty());
        when(albumRepository.save(any(Album.class))).thenReturn(new Album());
        when(albumImageRepository.save(any(AlbumImage.class))).thenReturn(new AlbumImage());

        boolean saveStatus = musicCatalogService.saveMusicData(musicResponse, genre);

        assertTrue(saveStatus);
        verify(soundRepository).existsByTitle(musicResponse.name());
        verify(artistRepository).findByName(musicResponse.artist_name());
        verify(artistRepository).save(any(Artist.class));
        verify(albumRepository).findByTitle(musicResponse.album_name());
        verify(albumRepository).save(any(Album.class));
        verify(albumImageRepository).save(any(AlbumImage.class));
        verify(soundRepository).save(any(Sound.class));
    }

    @Test
    void saveMusicData_ShouldReturnFalse_WhenSoundIsAlreadyExists(){
        Genre genre = new Genre();
        MusicResponse musicResponse = MusicDataFactory.musicResponse();

        when(soundRepository.existsByTitle(musicResponse.name())).thenReturn(true);

        boolean saveStatus = musicCatalogService.saveMusicData(musicResponse, genre);

        assertFalse(saveStatus);
        verifyNoInteractions(artistRepository);
        verifyNoInteractions(albumRepository);
        verifyNoInteractions(albumImageRepository);
        verifyNoMoreInteractions(soundRepository);
    }

}
