package org.musicservice.demo.repository.music;

import org.musicservice.demo.entity.music.Genre;
import org.musicservice.demo.entity.music.GenreName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
    Optional<Genre> findByName(GenreName name);
}
