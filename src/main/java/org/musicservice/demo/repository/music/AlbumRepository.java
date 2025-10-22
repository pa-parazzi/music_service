package org.musicservice.demo.repository.music;

import org.musicservice.demo.model.music.Album;
import org.musicservice.demo.model.music.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

    Optional<Album> findByTitle(String title);
}
