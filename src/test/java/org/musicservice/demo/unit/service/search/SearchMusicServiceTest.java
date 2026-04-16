package org.musicservice.demo.unit.service.search;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.musicservice.demo.dto.music.album.AlbumResponse;
import org.musicservice.demo.dto.music.artist.ArtistResponse;
import org.musicservice.demo.dto.music.common.PageResponse;
import org.musicservice.demo.dto.music.sound.SoundResponse;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.entity.music.Sound;
import org.musicservice.demo.exception.music.NoSuchMusicException;
import org.musicservice.demo.mapper.music.AlbumMapper;
import org.musicservice.demo.mapper.music.SoundMapper;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.musicservice.demo.repository.music.ArtistRepository;
import org.musicservice.demo.repository.music.SoundRepository;
import org.musicservice.demo.service.search.SearchMusicService;
import org.musicservice.demo.support.factory.unit.music.MusicDataFactory;
import org.springframework.data.domain.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SearchMusicServiceTest {

    @Mock
    private ArtistRepository artistRepository;
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private AlbumMapper albumMapper;
    @Mock
    private SoundRepository soundRepository;
    @Mock
    private SoundMapper soundMapper;

    @InjectMocks
    private SearchMusicService searchMusicService;

    private final String fragment = "just dance";
    private final int page = 0;
    private final int size = 2;
    private final Pageable pageableWithSortByIdAsc = PageRequest.of
            (page, size, Sort.by(Sort.Direction.ASC, "id"));

    @Test
    void getTracksByTitleStartingWith_ShouldReturnPageResponseOfSounds(){
        Sound sound1 = new Sound();
        Sound sound2 = new Sound();
        Page<Sound> soundsPage = new PageImpl<>(List.of(sound1, sound2), pageableWithSortByIdAsc, 3);

        SoundResponse soundResponse = MusicDataFactory.soundResponse();

        when(soundRepository.findByTitleStartingWithIgnoreCase
                (fragment, pageableWithSortByIdAsc)).thenReturn(soundsPage);
        when(soundMapper.toResponse(any(Sound.class))).thenReturn(soundResponse);

        PageResponse<SoundResponse> pageResponse = searchMusicService.getTracksByTitleStartingWith(fragment, page, size);
        assertPageResponse(pageResponse);
        assertEquals(soundResponse, pageResponse.contentList().get(0));
        assertEquals(soundResponse, pageResponse.contentList().get(1));
    }

    @Test
    void getTracksByTitleStartingWith_ShouldThrowNoSuchMusicException_WhenPageContentIsEmpty(){
        Page<Sound> soundsPage = new PageImpl<>(List.of(), pageableWithSortByIdAsc, 0);

        when(soundRepository.findByTitleStartingWithIgnoreCase
                (fragment, pageableWithSortByIdAsc)).thenReturn(soundsPage);

        assertThrows(NoSuchMusicException.class, () -> searchMusicService
                .getTracksByTitleStartingWith(fragment, page, size));

        verify(soundRepository).findByTitleStartingWithIgnoreCase(fragment, pageableWithSortByIdAsc);
        verifyNoInteractions(soundMapper);
    }

    @Test
    void getAlbumsByTitleStartingWith_ShouldReturnPageResponseOfAlbums(){
        Album album1 = new Album();
        Album album2 = new Album();
        Page<Album> albumsPage = new PageImpl<>(List.of(album1, album2), pageableWithSortByIdAsc, 3);

        AlbumResponse albumResponse = MusicDataFactory.albumResponse();

        when(albumRepository.findByTitleStartingWithIgnoreCase
                (fragment, pageableWithSortByIdAsc)).thenReturn(albumsPage);
        when(albumMapper.toAlbumResponse(any(Album.class))).thenReturn(albumResponse);

        PageResponse<AlbumResponse> pageResponse = searchMusicService.getAlbumsByTitleStartingWith(fragment, page, size);
        assertPageResponse(pageResponse);
        assertEquals(albumResponse, pageResponse.contentList().get(0));
        assertEquals(albumResponse, pageResponse.contentList().get(1));
    }

    @Test
    void getAlbumsByTitleStartingWith_ShouldThrowNoSuchMusicException_WhenPageContentIsEmpty(){
        Page<Album> albumsPage = new PageImpl<>(List.of(), pageableWithSortByIdAsc, 0);

        when(albumRepository.findByTitleStartingWithIgnoreCase
                (fragment, pageableWithSortByIdAsc)).thenReturn(albumsPage);

        assertThrows(NoSuchMusicException.class, () -> searchMusicService
                .getAlbumsByTitleStartingWith(fragment, page, size));

        verify(albumRepository).findByTitleStartingWithIgnoreCase(fragment, pageableWithSortByIdAsc);
        verifyNoInteractions(albumMapper);
    }

    @Test
    void getArtistsByNameStartingWith_ShouldReturnPageResponseOfArtists(){
        Artist artist1 = new Artist();
        Artist artist2 = new Artist();
        Page<Artist> artistsPage = new PageImpl<>(List.of(artist1, artist2), pageableWithSortByIdAsc, 3);

        when(artistRepository.findByNameStartingWithIgnoreCase
                (fragment, pageableWithSortByIdAsc)).thenReturn(artistsPage);

        PageResponse<ArtistResponse> pageResponse = searchMusicService.getArtistsByNameStartingWith(fragment, page, size);
        assertPageResponse(pageResponse);
    }

    @Test
    void getArtistsByNameStartingWith_ShouldThrowNoSuchMusicException_WhenPageContentIsEmpty(){
        Page<Artist> artistsPage = new PageImpl<>(List.of(), pageableWithSortByIdAsc, 3);

        when(artistRepository.findByNameStartingWithIgnoreCase
                (fragment, pageableWithSortByIdAsc)).thenReturn(artistsPage);

        assertThrows(NoSuchMusicException.class, () -> searchMusicService.getArtistsByNameStartingWith(fragment, page, size));
        verify(artistRepository).findByNameStartingWithIgnoreCase(fragment, pageableWithSortByIdAsc);
    }

    private <T> void assertPageResponse(PageResponse<T> pageResponse){
        assertEquals(size, pageResponse.contentList().size());
        assertTrue(pageResponse.hasNextPage());
    }
}
