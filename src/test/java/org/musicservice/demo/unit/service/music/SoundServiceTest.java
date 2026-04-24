package org.musicservice.demo.unit.service.music;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.musicservice.demo.dto.music.common.PageResponse;
import org.musicservice.demo.dto.music.sound.SoundResponse;
import org.musicservice.demo.dto.music.sound.SoundsResponse;
import org.musicservice.demo.entity.likes.SoundLike;
import org.musicservice.demo.entity.music.Sound;
import org.musicservice.demo.exception.music.MusicNotFoundException;
import org.musicservice.demo.mapper.music.SoundMapper;
import org.musicservice.demo.repository.music.SoundRepository;
import org.musicservice.demo.service.likes.SoundLikeService;
import org.musicservice.demo.service.music.SoundService;
import org.musicservice.demo.support.factory.unit.music.MusicDataFactory;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SoundServiceTest {

    @Mock
    private SoundRepository soundRepository;
    @Mock
    private SoundLikeService soundLikeService;
    @Mock
    private SoundMapper soundMapper;

    @InjectMocks
    private SoundService soundService;

    private final int page = 0;
    private final int size = 2;
    private final Pageable pageable = PageRequest.of(page, size);
    private final Pageable pageableWithSortByIdAsc = PageRequest.of
            (page, size, Sort.by(Sort.Direction.ASC, "id"));

    @Test
    void getSoundsByArtistIdPaged_ShouldReturnPageResponseOfSounds(){
        Long artistId = 1L;
        Sound sound1 = new Sound();
        Sound sound2 = new Sound();
        Page<Sound> soundsPage = new PageImpl<>(List.of(sound1, sound2), pageableWithSortByIdAsc, 3);
        SoundResponse soundResponse = MusicDataFactory.soundResponse();

        when(soundRepository.findByArtistId(artistId, pageableWithSortByIdAsc)).thenReturn(soundsPage);
        when(soundMapper.toResponse(any(Sound.class))).thenReturn(soundResponse);

        PageResponse<SoundResponse> pageResponse = soundService.getSoundsByArtistIdPaged(artistId, page, size);
        assertPageResponseOfSounds(pageResponse, soundResponse);
        verify(soundRepository).findByArtistId(artistId, pageableWithSortByIdAsc);
        verify(soundMapper).toResponse(sound1);
        verify(soundMapper).toResponse(sound2);
    }

    @Test
    void getSoundsByArtistIdPaged_ShouldThrowMusicNotFoundException_WhenPageContentIsEmpty(){
        Long artistId = 1L;
        Page<Sound> soundsPage = new PageImpl<>(List.of(), pageableWithSortByIdAsc, 0);

        when(soundRepository.findByArtistId(artistId, pageableWithSortByIdAsc)).thenReturn(soundsPage);

        assertThrows(MusicNotFoundException.class, () -> soundService.getSoundsByArtistIdPaged(artistId, page, size));
        verify(soundRepository).findByArtistId(artistId, pageableWithSortByIdAsc);
        verifyNoInteractions(soundMapper);
    }

    @Test
    void getSoundsByAlbumId_ShouldReturnPageResponseOfSounds(){
        Long albumId = 1L;
        Sound sound1 = new Sound();
        Sound sound2 = new Sound();
        List<Sound> sounds = List.of(sound1, sound2);
        SoundResponse soundResponse = MusicDataFactory.soundResponse();

        when(soundRepository.findAllByAlbumId(albumId)).thenReturn(sounds);
        when(soundMapper.toResponse(any(Sound.class))).thenReturn(soundResponse);

        SoundsResponse soundsResponse = soundService.getSoundsByAlbumId(albumId);
        assertEquals(soundsResponse.sounds().size(), sounds.size());
        verify(soundRepository).findAllByAlbumId(albumId);
        verify(soundMapper).toResponse(sound1);
        verify(soundMapper).toResponse(sound2);
    }

    @Test
    void getSoundsByAlbumId_ShouldThrowMusicNotFoundException_WhenResultIsEmpty(){
        Long albumId = 1L;
        List<Sound> sounds = List.of();

        when(soundRepository.findAllByAlbumId(albumId)).thenReturn(sounds);

        assertThrows(MusicNotFoundException.class, () -> soundService.getSoundsByAlbumId(albumId));
        verify(soundRepository).findAllByAlbumId(albumId);
        verifyNoInteractions(soundMapper);
    }

    @Test
    void getSoundsByGenreIdPaged_ShouldReturnPageResponseOfSounds(){
        Long genreId = 1L;
        Sound sound1 = new Sound();
        Sound sound2 = new Sound();
        Page<Sound> soundsPage = new PageImpl<>(List.of(sound1, sound2), pageableWithSortByIdAsc, 3);
        SoundResponse soundResponse = MusicDataFactory.soundResponse();

        when(soundRepository.findByGenreId(genreId, pageableWithSortByIdAsc)).thenReturn(soundsPage);
        when(soundMapper.toResponse(any(Sound.class))).thenReturn(soundResponse);

        PageResponse<SoundResponse> pageResponse = soundService.getSoundsByGenreIdPaged(genreId, page, size);
        assertPageResponseOfSounds(pageResponse, soundResponse);
        verify(soundRepository).findByGenreId(genreId, pageableWithSortByIdAsc);
        verify(soundMapper).toResponse(sound1);
        verify(soundMapper).toResponse(sound2);
    }

    @Test
    void getSoundsByGenreIdPaged_ShouldThrowMusicNotFoundException_WhenPageContentIsEmpty(){
        Long genreId = 1L;
        Page<Sound> soundsPage = new PageImpl<>(List.of(), pageableWithSortByIdAsc, 0);

        when(soundRepository.findByGenreId(genreId, pageableWithSortByIdAsc)).thenReturn(soundsPage);

        assertThrows(MusicNotFoundException.class, () -> soundService.getSoundsByGenreIdPaged(genreId, page, size));
        verify(soundRepository).findByGenreId(genreId, pageableWithSortByIdAsc);
        verifyNoInteractions(soundMapper);
    }

    @Test
    void getTrackCollectionByUserId_ShouldReturnPageResponseOfSounds(){
        Long userId = 1L;
        Sound sound1 = new Sound();
        Sound sound2 = new Sound();
        SoundLike like1 = new SoundLike();
        like1.setSound(sound1);
        SoundLike like2 = new SoundLike();
        like2.setSound(sound2);
        Page<SoundLike> soundLikesPage = new PageImpl<>(List.of(like1, like2), pageable, 3);
        SoundResponse soundResponse = MusicDataFactory.soundResponse();

        when(soundLikeService.findSoundLikesByUserid(userId, pageable)).thenReturn(soundLikesPage);
        when(soundMapper.toResponse(any(Sound.class))).thenReturn(soundResponse);

        PageResponse<SoundResponse> pageResponse = soundService.getTrackCollectionByUserId(userId, page, size);
        assertPageResponseOfSounds(pageResponse, soundResponse);
        verify(soundLikeService).findSoundLikesByUserid(userId, pageable);
        verify(soundMapper).toResponse(sound1);
        verify(soundMapper).toResponse(sound2);
    }

    @Test
    void getSoundPageResponseById_ShouldReturnSoundPageResponse(){
        Long id = 1L;
        Sound sound = new Sound();

        when(soundRepository.findByIdForSoundPage(id)).thenReturn(Optional.of(sound));

        soundService.getSoundPageResponseById(id);

        verify(soundRepository).findByIdForSoundPage(id);
        verify(soundMapper).toPageResponse(sound);
    }

    @Test
    void getSoundPageResponseById_ShouldThrowMusicNotFoundException_WhenEntityIsEmpty(){
        Long id = 1L;
        when(soundRepository.findByIdForSoundPage(id)).thenReturn(Optional.empty());

        assertThrows(MusicNotFoundException.class, () -> soundService.getSoundPageResponseById(id));
        verifyNoInteractions(soundMapper);
    }

    private void assertPageResponseOfSounds(PageResponse<SoundResponse> response,
                                            SoundResponse expectedResponseFromMapper){
        assertEquals(size, response.content().size());
        assertEquals(expectedResponseFromMapper, response.content().get(0));
        assertEquals(expectedResponseFromMapper, response.content().get(1));
        assertTrue(response.hasNextPage());
    }
}
