package org.musicservice.demo.service.like;

import jakarta.persistence.EntityManager;
import org.musicservice.demo.dto.like.LikedAlbumResponse;
import org.musicservice.demo.dto.like.UserGetLikesRequest;
import org.musicservice.demo.dto.like.UserLikeRequest;
import org.musicservice.demo.mapper.like.LikeAlbumMapper;
import org.musicservice.demo.model.like.LikeAlbum;
import org.musicservice.demo.model.music.Album;
import org.musicservice.demo.model.user.User;
import org.musicservice.demo.repository.like.LikeAlbumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class LikeAlbumService {

    private final LikeAlbumRepository likeAlbumRepository;
    private final LikeAlbumMapper likeAlbumMapper;
    private final EntityManager entityManager;

    @Autowired
    public LikeAlbumService(LikeAlbumRepository likeAlbumRepository, LikeAlbumMapper likeAlbumMapper, EntityManager entityManager) {
        this.likeAlbumRepository = likeAlbumRepository;
        this.likeAlbumMapper = likeAlbumMapper;
        this.entityManager = entityManager;
    }

    @Transactional
    public void create(UserLikeRequest request){
        User user = entityManager.getReference(User.class, request.getUserId());
        Album album = entityManager.getReference(Album.class, request.getTargetId());
        LikeAlbum likeAlbum = new LikeAlbum(user, album);
        likeAlbumRepository.save(likeAlbum);
    }

    @Transactional
    public void delete(UserLikeRequest request){
        likeAlbumRepository.deleteByUserIdAndAlbumId(request.getUserId(), request.getTargetId());
    }

    public List<LikedAlbumResponse> getAllLikedAlbums(UserGetLikesRequest request){
        return likeAlbumRepository.findAllByUserIdOrderByCreatedAtDesc(request.getUserId()).stream().map(likeAlbumMapper::toResponse).toList();
    }

}
