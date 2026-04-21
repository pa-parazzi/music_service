package org.musicservice.demo.service.likes;

import jakarta.persistence.EntityManager;
import org.musicservice.demo.dto.likes.LikeStatusResponse;
import org.musicservice.demo.dto.likes.LikedContentIds;
import org.musicservice.demo.entity.likes.SoundLike;
import org.musicservice.demo.entity.music.Sound;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.exception.music.NoSuchMusicException;
import org.musicservice.demo.repository.likes.SoundLikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public LikedContentIds findAllLikedSoundIds(Long userId){
        List<Long> likedSoundIds = soundLikeRepository.findAllByUserId(userId).stream()
                .map(soundLike -> soundLike.getSound().getId()).toList();
        return new LikedContentIds(likedSoundIds);
    }

    public Page<SoundLike> findSoundLikesByUserid(Long userId, Pageable pageable){
        Page<SoundLike> pageResponse = soundLikeRepository.findByUserIdOrderByCreatedAtDescIdDesc(userId, pageable);
        if(pageResponse.getContent().isEmpty()) throw new NoSuchMusicException("У вас нет понравившихся песен");
        return pageResponse;
    }

    public LikeStatusResponse findLikedSound(Long userId, Long soundId){
        return new LikeStatusResponse(soundLikeRepository.existsByUserIdAndSoundId(userId, soundId));
    }
}
