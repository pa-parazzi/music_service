package org.musicservice.demo.repository.music;

import org.musicservice.demo.dto.music.artist.ArtistResponse;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.entity.music.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {

    Optional<Artist> findByName(String name);

    List<ArtistResponse> findAllArtistResponseByNameStartingWith(String fragment);

    Optional<ArtistResponse> findArtistResponseById(Long id);

    List<ArtistResponse> findAllArtistResponseByGenreId(Long id);

}