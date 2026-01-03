package org.musicservice.demo.repository.like;

import org.musicservice.demo.model.like.LikeSound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeSoundRepository extends JpaRepository<LikeSound, Long> {

    @Modifying
    @Query("delete from LikeSound l where l.user.id = :userId and l.sound.id = :soundId")
    void deleteByUserIdAndSoundId(Long userId, Long soundId);

    List<LikeSound> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}
