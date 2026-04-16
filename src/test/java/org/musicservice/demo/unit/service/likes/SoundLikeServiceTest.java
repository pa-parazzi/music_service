package org.musicservice.demo.unit.service.likes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.musicservice.demo.entity.likes.SoundLike;
import org.musicservice.demo.exception.music.NoSuchMusicException;
import org.musicservice.demo.repository.likes.SoundLikeRepository;
import org.musicservice.demo.service.likes.SoundLikeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SoundLikeServiceTest {

    @Mock
    private SoundLikeRepository soundLikeRepository;

    @InjectMocks
    private SoundLikeService soundLikeService;

    private final Pageable pageable = PageRequest.of(0, 2);

    @Test
    void findSoundLikesByUserid_ShouldReturnPageOfSoundLikes(){
        Long userId = 1L;
        SoundLike like1 = new SoundLike();
        SoundLike like2 = new SoundLike();
        Page<SoundLike> soundLikesPage = new PageImpl<>(List.of(like1, like2), pageable, 3);

        when(soundLikeRepository.findByUserIdOrderByCreatedAtDescIdDesc(userId, pageable)).thenReturn(soundLikesPage);

        Page<SoundLike> pageResponse = soundLikeService.findSoundLikesByUserid(userId, pageable);

        assertSame(soundLikesPage, pageResponse);
        assertEquals(2, pageResponse.getContent().size());
        assertTrue(pageResponse.hasNext());
        verify(soundLikeRepository).findByUserIdOrderByCreatedAtDescIdDesc(userId, pageable);
    }

    @Test
    void findSoundLikesByUserid_ShouldThrowException_WhenPageContentIsEmpty(){
        Long userId = 1L;
        Page<SoundLike> soundLikesPage = new PageImpl<>(List.of(), pageable, 0);

        when(soundLikeRepository.findByUserIdOrderByCreatedAtDescIdDesc(userId, pageable)).thenReturn(soundLikesPage);

        assertThrows(NoSuchMusicException.class, () -> soundLikeService.findSoundLikesByUserid(userId, pageable));
        verify(soundLikeRepository).findByUserIdOrderByCreatedAtDescIdDesc(userId, pageable);
    }
}
