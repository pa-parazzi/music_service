package org.musicservice.demo.service.like;

import jakarta.persistence.EntityManager;
import org.musicservice.demo.dto.like.LikedSoundId;
import org.musicservice.demo.dto.like.LikedSounds;
import org.musicservice.demo.dto.like.UserGetLikesRequest;
import org.musicservice.demo.dto.like.UserLikedMusicRequest;
import org.musicservice.demo.mapper.like.LikeSoundMapper;
import org.musicservice.demo.entity.like.LikeSound;
import org.musicservice.demo.entity.music.Sound;
import org.musicservice.demo.entity.user.User;
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
    public void create(UserLikedMusicRequest request){
        User user = entityManager.getReference(User.class, request.userId());
        Sound sound = entityManager.getReference(Sound.class, request.targetId());
        LikeSound likeSound = new LikeSound(user, sound);
        likeSoundRepository.save(likeSound);
    }

    @Transactional
    public void delete(UserLikedMusicRequest request){
        likeSoundRepository.deleteByUserIdAndSoundId(request.userId(), request.targetId());
    }

    public LikedSounds getAllLikedSounds(UserGetLikesRequest request){
        List<LikedSoundId> likedSoundsIdsList = likeSoundRepository.findAllByUserIdOrderByCreatedAtDesc(request.userId()).stream().map(likeSoundMapper::toResponse).toList();
        return new LikedSounds(likedSoundsIdsList);
    }
}
