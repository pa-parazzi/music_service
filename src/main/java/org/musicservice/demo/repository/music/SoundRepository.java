package org.musicservice.demo.repository.music;

import jakarta.annotation.Nonnull;
import org.musicservice.demo.model.music.Artist;
import org.musicservice.demo.model.music.Sound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SoundRepository extends JpaRepository<Sound, Long> {

}
