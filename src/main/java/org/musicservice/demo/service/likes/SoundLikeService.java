package org.musicservice.demo.service.likes;

import jakarta.persistence.EntityManager;
import org.musicservice.demo.dto.likes.LikedSounds;
import org.musicservice.demo.entity.likes.SoundLike;
import org.musicservice.demo.entity.music.Sound;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.repository.likes.SoundLikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SoundLikeService {

    private final SoundLikeRepository soundLikeRepository;
    private final EntityManager entityManager;

    @Autowired
    public SoundLikeService(SoundLikeRepository soundLikeRepository, EntityManager entityManager) {
        this.soundLikeRepository = soundLikeRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public void create(Long userId, Long soundId){
        User user = entityManager.getReference(User.class, userId);
        Sound sound = entityManager.getReference(Sound.class, soundId);
        SoundLike soundLike = new SoundLike(user, sound);
        soundLikeRepository.save(soundLike);
    }

    @Transactional
    public void delete(Long userId, Long soundId){
        soundLikeRepository.deleteByUserIdAndSoundId(userId, soundId);
    }

    public LikedSounds getAllLikedSounds(Long userId){
        List<Long> likedSoundsIdsList = soundLikeRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(soundLike -> soundLike.getSound().getId()).toList();
        return new LikedSounds(likedSoundsIdsList);
    }
}
