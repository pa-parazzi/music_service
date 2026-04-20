package org.musicservice.demo.unit.service.music;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.musicservice.demo.dto.music.album.AlbumResponse;
import org.musicservice.demo.dto.music.common.PageResponse;
import org.musicservice.demo.entity.likes.AlbumLike;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.exception.music.MusicNotFoundException;
import org.musicservice.demo.exception.music.NoSuchMusicException;
import org.musicservice.demo.mapper.music.AlbumMapper;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.musicservice.demo.service.likes.AlbumLikeService;
import org.musicservice.demo.service.music.AlbumService;
import org.musicservice.demo.support.factory.unit.music.MusicDataFactory;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AlbumServiceTest {

    @Mock
    private AlbumRepository albumRepository;
    @Mock
    private AlbumLikeService albumLikeService;
    @Mock
    private AlbumMapper albumMapper;

    @InjectMocks
    private AlbumService albumService;

    private final int page = 0;
    private final int size = 2;
    private final Pageable pageable = PageRequest.of(page, size);
    private final Pageable pageableWithSortByIdAsc = PageRequest.of
            (page, size, Sort.by(Sort.Direction.ASC, "id"));

    @Test
    void getAlbumCollectionByUserId_ShouldReturnPageResponseOfAlbums(){
        Long userId = 1L;
        Album album1 = new Album();
        Album album2 = new Album();
        AlbumLike like1 = new AlbumLike();
        like1.setAlbum(album1);
        AlbumLike like2 = new AlbumLike();
        like2.setAlbum(album2);
        Page<AlbumLike> albumLikesPage = new PageImpl<>(List.of(like1, like2), pageable, 3);
        AlbumResponse albumResponse = MusicDataFactory.albumResponse();

        when(albumLikeService.findAlbumLikesByUserId(userId, pageable)).thenReturn(albumLikesPage);
        when(albumMapper.toAlbumResponse(any(Album.class))).thenReturn(albumResponse);

        PageResponse<AlbumResponse> pageResponse = albumService.getAlbumCollectionByUserId(userId, page, size);

        assertPageResponseOfAlbums(pageResponse, albumResponse);
        verify(albumLikeService).findAlbumLikesByUserId(userId, pageable);
        verify(albumMapper).toAlbumResponse(album1);
        verify(albumMapper).toAlbumResponse(album2);
    }

    @Test
    void findByIdWithArtistAndImage_ShouldReturnAlbumResponse(){
        Long id = 1L;
        Album album = new Album();
        AlbumResponse albumResponse = MusicDataFactory.albumResponse();

        when(albumRepository.findByIdWithArtistAndImage(id)).thenReturn(Optional.of(album));
        when(albumMapper.toAlbumResponse(album)).thenReturn(albumResponse);

        AlbumResponse actualResponse = albumService.findByIdWithArtistAndImage(id);

        assertSame(albumResponse, actualResponse);
        verify(albumRepository).findByIdWithArtistAndImage(id);
        verify(albumMapper).toAlbumResponse(album);
    }

    @Test
    void findByIdWithArtistAndImage_ShouldThrowException_WhenReturnsValueIsEmpty(){
        Long albumId = 1L;

        when(albumRepository.findByIdWithArtistAndImage(albumId)).thenReturn(Optional.empty());

        assertThrows(MusicNotFoundException.class, ()-> albumService.findByIdWithArtistAndImage(albumId));
        verifyNoInteractions(albumMapper);
    }

    @Test
    void findAlbumsByGenreIdPaged_ShouldReturnPageResponseOfAlbums(){
        Long genreId = 1L;
        Album album1 = new Album();
        Album album2 = new Album();
        List<Album> albums = List.of(album1, album2);
        Page<Album> albumsPage = new PageImpl<>(albums, pageableWithSortByIdAsc, 3);
        AlbumResponse albumResponse = MusicDataFactory.albumResponse();

        when(albumRepository.findByGenreId(genreId, pageableWithSortByIdAsc)).thenReturn(albumsPage);
        when(albumMapper.toAlbumResponse(any(Album.class))).thenReturn(albumResponse);

        PageResponse<AlbumResponse> pageResponse = albumService.findAlbumsByGenreIdPaged(genreId, page, size);

        assertPageResponseOfAlbums(pageResponse, albumResponse);
        verify(albumRepository).findByGenreId(genreId, pageableWithSortByIdAsc);
        verify(albumMapper).toAlbumResponse(album1);
        verify(albumMapper).toAlbumResponse(album2);
    }

    @Test
    void findAlbumsByGenreIdPaged_ShouldThrowException_WhenPageContentIsEmpty(){
        Long genreId = 1L;
        Page<Album> albumsPage = new PageImpl<>(List.of(), pageableWithSortByIdAsc, 0);

        when(albumRepository.findByGenreId(genreId, pageableWithSortByIdAsc)).thenReturn(albumsPage);

        assertThrows(NoSuchMusicException.class, () -> albumService.findAlbumsByGenreIdPaged(genreId, page, size));

        verify(albumRepository).findByGenreId(genreId, pageableWithSortByIdAsc);
        verifyNoInteractions(albumMapper);
    }

    private void assertPageResponseOfAlbums(PageResponse<AlbumResponse> response,
                                            AlbumResponse expectedResponseFromMapper){
        assertEquals(size, response.content().size());
        assertEquals(expectedResponseFromMapper, response.content().get(0));
        assertEquals(expectedResponseFromMapper, response.content().get(1));
        assertTrue(response.hasNextPage());
    }
}
