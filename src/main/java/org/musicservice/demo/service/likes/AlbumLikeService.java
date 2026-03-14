package org.musicservice.demo.service.likes;

import jakarta.persistence.EntityManager;
import org.musicservice.demo.dto.likes.LikeStatusResponse;
import org.musicservice.demo.dto.likes.LikedContentIds;
import org.musicservice.demo.entity.likes.AlbumLike;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.repository.likes.AlbumLikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public LikedContentIds getAllLikedAlbums(Long userId){
        List<Long> likedAlbumsIdsList = albumLikeRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(albumLike-> albumLike.getAlbum().getId()).toList();
        return new LikedContentIds(likedAlbumsIdsList);
    }

    public LikeStatusResponse findLikedAlbum(Long userId, Long albumId) {
        return new LikeStatusResponse(albumLikeRepository.existsByUserIdAndAlbumId(userId, albumId));
    }
}
