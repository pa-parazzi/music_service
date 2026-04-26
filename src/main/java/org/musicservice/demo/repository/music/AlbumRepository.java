package org.musicservice.demo.repository.music;

import org.musicservice.demo.entity.music.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

    Optional<Album> findByTitle(String title);

    @Query("select a from Album a join fetch a.artist join fetch a.image where a.id= :id")
    Optional<Album> findByIdWithArtistAndImage(@Param("id") Long id);

    @EntityGraph(attributePaths = {"artist", "image"})
    Page<Album> findByTitleStartingWithIgnoreCase(String prefix, Pageable pageable);

    @EntityGraph(attributePaths = {"artist", "image"})
    Page<Album> findByGenreId(Long genreId, Pageable pageable);

    @EntityGraph(attributePaths = {"artist", "image"})
    Page<Album> findAlbumsOrderByReleaseDateBetween
            (LocalDate releaseDateAfter,
             LocalDate releaseDateBefore,
             Pageable pageable);
}
