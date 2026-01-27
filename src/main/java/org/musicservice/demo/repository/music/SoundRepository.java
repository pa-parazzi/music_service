package org.musicservice.demo.repository.music;

import org.musicservice.demo.entity.music.Sound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SoundRepository extends JpaRepository<Sound, Long> {

    Optional<Sound> findByTitle(String title);

    @Query(value = "select s.* FROM Sound s join unnest(:ids) with ordinality t(id, ord) ON s.id = t.id order by t.ord", nativeQuery = true)
    List<Sound> findAllByIdForCollectionPage(@Param("ids") Long[] ids);

    List<Sound> findAllByArtistId(Long artistId);

    List<Sound> findAllByAlbumId(Long albumId);
}
