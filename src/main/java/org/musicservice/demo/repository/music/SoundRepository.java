package org.musicservice.demo.repository.music;

import org.musicservice.demo.dto.music.sound.SoundPageProjection;
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

    Page<Sound> findAllByTitleStartingWithIgnoreCase(String prefix, Pageable pageable);

    List<Sound> findAllByArtistId(Long artistId);

    List<Sound> findAllByAlbumId(Long albumId);

    Page<Sound> findAllByGenreId(Long genreId, Pageable pageable);

    boolean existsByKey(String key);

    @Query("""
    select new org.musicservice.demo.dto.music.sound.SoundPageProjection(
        s.title, s.duration, s.key, s.releaseDate,
        new org.musicservice.demo.dto.music.artist.ArtistResponse (a.id, a.name),
        new org.musicservice.demo.dto.music.album.AlbumInfo(al.id, al.title, al.image.key, null))
        from Sound s join s.artist a join s.album al where s.id=:id
    """)
    Optional<SoundPageProjection> findByIdForSoundPage(Long id);
}
