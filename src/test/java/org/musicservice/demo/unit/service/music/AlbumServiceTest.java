package org.musicservice.demo.unit.service.music;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.musicservice.demo.exception.music.MusicNotFoundException;
import org.musicservice.demo.mapper.music.AlbumMapper;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.musicservice.demo.service.music.AlbumService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AlbumServiceTest {

    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private AlbumMapper albumMapper;

    @InjectMocks
    private AlbumService albumService;

    @Test
    void findByIdWithArtistAndImage_ShouldThrowsApiNotFoundException(){
        Long albumId = 1L;

        when(albumRepository.findByIdWithArtistAndImage(albumId)).thenReturn(Optional.empty());

        assertThrows(MusicNotFoundException.class, ()-> albumService.findByIdWithArtistAndImage(albumId));
        verifyNoInteractions(albumMapper);
    }
}
