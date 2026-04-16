package org.musicservice.demo.service.likes;

import jakarta.persistence.EntityManager;
import org.musicservice.demo.dto.likes.LikeStatusResponse;
import org.musicservice.demo.entity.likes.AlbumLike;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.exception.music.NoSuchMusicException;
import org.musicservice.demo.repository.likes.AlbumLikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AlbumLikeService {

    private final AlbumLikeRepository albumLikeRepository;
    private final EntityManager entityManager;

    @Autowired
    public AlbumLikeService(AlbumLikeRepository albumLikeRepository, EntityManager entityManager) {
        this.albumLikeRepository = albumLikeRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public void create(Long userId, Long albumId){
        User user = entityManager.getReference(User.class, userId);
        Album album = entityManager.getReference(Album.class, albumId);
        AlbumLike albumLike = new AlbumLike(user, album);
        albumLikeRepository.save(albumLike);
    }

    @Transactional
    public void delete(Long userId, Long albumId){
        albumLikeRepository.deleteByUserIdAndAlbumId(userId, albumId);
    }

    public Page<AlbumLike> findAlbumLikesByUserId(Long userId, Pageable pageable){
        Page<AlbumLike> pageResponse = albumLikeRepository.findByUserIdOrderByCreatedAtDescIdDesc(userId, pageable);
        if(pageResponse.getContent().isEmpty()) throw new NoSuchMusicException("У вас нет понравившихся альбомов");
        return pageResponse;
    }

    public LikeStatusResponse findLikedAlbum(Long userId, Long albumId) {
        return new LikeStatusResponse(albumLikeRepository.existsByUserIdAndAlbumId(userId, albumId));
    }
}
