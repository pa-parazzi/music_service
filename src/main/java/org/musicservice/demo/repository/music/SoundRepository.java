package org.musicservice.demo.repository.music;

import org.musicservice.demo.entity.music.Sound;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SoundRepository extends JpaRepository<Sound, Long> {

    @Query("select s from Sound s join fetch s.artist join fetch s.album join fetch s.album.image where s.id = :id")
    Optional<Sound> findByIdForSoundPage(Long id);

    List<Sound> findAllByAlbumId(Long albumId);

    Page<Sound> findByTitleStartingWithIgnoreCase(String prefix, Pageable pageable);

    Page<Sound> findByArtistId(Long artistId, Pageable pageable);

    Page<Sound> findByGenreId(Long genreId, Pageable pageable);

    boolean existsByTitle(String name);
}
