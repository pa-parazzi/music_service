package org.musicservice.demo.unit.service.music;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.musicservice.demo.exception.ApiNotFoundException;
import org.musicservice.demo.repository.music.ArtistRepository;
import org.musicservice.demo.service.music.ArtistService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ArtistServiceTest {

    @Mock
    private ArtistRepository artistRepository;

    @InjectMocks
    private ArtistService artistService;

    @Test
    void viewArtistById_ShouldThrowsApiNotFoundException(){
        Long artistId = 1L;

        when(artistRepository.findArtistResponseById(artistId)).thenReturn(Optional.empty());

        assertThrows(ApiNotFoundException.class, ()-> artistService.viewArtistById(artistId));
    }
}
