package org.musicservice.demo.repository.music;

import org.hibernate.annotations.processing.SQL;
import org.musicservice.demo.model.music.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserId(Long userId);

    void deleteByUserIdAndTarget(Long userId, Object target);

    List<Like> findAllByUserId(Long userId);
}
