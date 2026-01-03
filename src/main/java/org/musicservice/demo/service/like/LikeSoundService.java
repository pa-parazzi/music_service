package org.musicservice.demo.service.like;

import jakarta.persistence.EntityManager;
import org.musicservice.demo.dto.like.LikedSoundResponse;
import org.musicservice.demo.dto.like.UserGetLikesRequest;
import org.musicservice.demo.dto.like.UserLikeRequest;
import org.musicservice.demo.mapper.like.LikeSoundMapper;
import org.musicservice.demo.model.like.LikeSound;
import org.musicservice.demo.model.music.Sound;
import org.musicservice.demo.model.user.User;
import org.musicservice.demo.repository.like.LikeSoundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class LikeSoundService {

    private final LikeSoundRepository likeSoundRepository;
    private final LikeSoundMapper likeSoundMapper;
    private final EntityManager entityManager;

    @Autowired
    public LikeSoundService(LikeSoundRepository likeSoundRepository, LikeSoundMapper likeSoundMapper, EntityManager entityManager) {
        this.likeSoundRepository = likeSoundRepository;
        this.likeSoundMapper = likeSoundMapper;
        this.entityManager = entityManager;
    }

    @Transactional
    public void create(UserLikeRequest request){
        User user = entityManager.getReference(User.class, request.getUserId());
        Sound sound = entityManager.getReference(Sound.class, request.getTargetId());
        LikeSound likeSound = new LikeSound(user, sound);
        likeSoundRepository.save(likeSound);
    }

    @Transactional
    public void delete(UserLikeRequest request){
        likeSoundRepository.deleteByUserIdAndSoundId(request.getUserId(), request.getTargetId());
    }

    public List<LikedSoundResponse> getAllLikedSounds(UserGetLikesRequest request){
        return likeSoundRepository.findAllByUserIdOrderByCreatedAtDesc(request.getUserId()).stream().map(likeSoundMapper::toResponse).toList();
    }
}
