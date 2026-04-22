package org.musicservice.demo.repository.likes;

import org.musicservice.demo.entity.likes.SoundLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SoundLikeRepository extends JpaRepository<SoundLike, Long> {

    void deleteByUserIdAndSoundId(Long userId, Long soundId);

    @EntityGraph(attributePaths = "sound")
    Page<SoundLike> findByUserIdOrderByCreatedAtDescIdDesc(Long userId, Pageable pageable);

    boolean existsByUserIdAndSoundId(Long userId, Long soundId);

    List<SoundLike> findAllByUserId(Long userId);
}
