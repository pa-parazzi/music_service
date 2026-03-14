package org.musicservice.demo.repository.likes;

import org.musicservice.demo.entity.likes.SoundLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SoundLikeRepository extends JpaRepository<SoundLike, Long> {

    @Modifying
    @Query("delete from SoundLike l where l.user.id = :userId and l.sound.id = :soundId")
    void deleteByUserIdAndSoundId(Long userId, Long soundId);

    List<SoundLike> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    boolean existsByUserIdAndSoundId(Long userId, Long soundId);
}
