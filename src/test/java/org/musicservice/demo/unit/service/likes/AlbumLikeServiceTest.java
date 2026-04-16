package org.musicservice.demo.unit.service.likes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.musicservice.demo.entity.likes.AlbumLike;
import org.musicservice.demo.exception.music.NoSuchMusicException;
import org.musicservice.demo.repository.likes.AlbumLikeRepository;
import org.musicservice.demo.service.likes.AlbumLikeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AlbumLikeServiceTest {

    @Mock
    private AlbumLikeRepository albumLikeRepository;

    @InjectMocks
    private AlbumLikeService albumLikeService;

    private final Pageable pageable = PageRequest.of(0, 2);

    @Test
    void findAlbumLikesByUserId_ShouldReturnPageOfAlbumLikes(){
        Long userId = 1L;
        AlbumLike like1 = new AlbumLike();
        AlbumLike like2 = new AlbumLike();
        Page<AlbumLike> albumLikesPage = new PageImpl<>(List.of(like1, like2), pageable, 3);

        when(albumLikeRepository.findByUserIdOrderByCreatedAtDescIdDesc
                (userId, pageable)).thenReturn(albumLikesPage);

        Page<AlbumLike> pageResponse = albumLikeService.findAlbumLikesByUserId(userId, pageable);

        assertSame(pageResponse, albumLikesPage);
        assertEquals(2, pageResponse.getContent().size());
        assertTrue(pageResponse.hasNext());
        verify(albumLikeRepository).findByUserIdOrderByCreatedAtDescIdDesc(userId, pageable);
    }

    @Test
    void findAlbumLikesByUserId_ShouldThrowException_WhenPageContentIsEmpty(){
        Long userId = 1L;
        Page<AlbumLike> albumLikesPage = new PageImpl<>(List.of(), pageable, 0);
        when(albumLikeRepository.findByUserIdOrderByCreatedAtDescIdDesc(userId, pageable)).thenReturn(albumLikesPage);

        assertThrows(NoSuchMusicException.class, () -> albumLikeService.findAlbumLikesByUserId(userId, pageable));
        verify(albumLikeRepository).findByUserIdOrderByCreatedAtDescIdDesc(userId, pageable);
    }
}
