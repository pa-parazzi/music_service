package org.musicservice.demo.unit.service.search;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.musicservice.demo.dto.music.album.AlbumResponse;
import org.musicservice.demo.dto.music.artist.ArtistResponse;
import org.musicservice.demo.dto.music.search.SearchMusicResponse;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.exception.music.NoSuchMusicResultException;
import org.musicservice.demo.mapper.music.AlbumMapper;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.musicservice.demo.repository.music.ArtistRepository;
import org.musicservice.demo.service.search.SearchMusicService;
import org.musicservice.demo.support.factory.unit.music.AlbumDataFactory;
import org.musicservice.demo.support.factory.unit.music.ArtistDataFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SearchMusicServiceTest {

    @Mock
    private ArtistRepository artistRepository;
    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private AlbumMapper albumMapper;

    @InjectMocks
    private SearchMusicService searchMusicService;

    @Test
    void searchMusicResult_ShouldReturnResultWithArtistAndAlbums(){
        String fragment = "Black Hole";
        Album album = AlbumDataFactory.album();
        AlbumResponse albumResponse = AlbumDataFactory.albumResponse();
        List<ArtistResponse> artistResponseList = List.of(ArtistDataFactory.artistResponse());
        List<Album> albumList = List.of(album);

        when(artistRepository.findAllArtistResponseByNameStartingWith(fragment)).thenReturn(artistResponseList);
        when(albumRepository.findAllByTitleStartingWith(fragment)).thenReturn(albumList);
        when(albumMapper.toAlbumResponse(album)).thenReturn(albumResponse);

        SearchMusicResponse result = searchMusicService.searchMusicResult(fragment);

        assertNotNull(result.artists());
        assertNotNull(result.albums());
        verifyNoMoreInteractions(artistRepository, albumRepository, albumMapper);
    }

    @Test
    void searchMusicResult_ShouldReturnNull_WhenFragmentIsEmpty(){
        String fragment = " ";

        assertThrows(NoSuchMusicResultException.class, ()-> searchMusicService.searchMusicResult(fragment));

        verifyNoInteractions(artistRepository, albumRepository, albumMapper);
    }

    @Test
    void searchMusicResult_ShouldReturnNull_WhenFragmentIsNull(){
        assertThrows(NoSuchMusicResultException.class, ()-> searchMusicService.searchMusicResult(null));

        verifyNoInteractions(artistRepository, albumRepository, albumMapper);
    }
}
