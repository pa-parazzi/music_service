package org.musicservice.demo.repository.music;

import org.musicservice.demo.entity.music.Sound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SoundRepository extends JpaRepository<Sound, Long> {

    Optional<Sound> findByTitle(String title);

    @Query("select s from Sound s where s.id in :ids")
    List<Sound> findAllByIdForCollectionPage(Iterable<Long> ids);
}
