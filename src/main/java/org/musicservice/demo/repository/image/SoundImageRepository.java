package org.musicservice.demo.repository.image;

import org.musicservice.demo.model.image.SoundImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SoundImageRepository extends JpaRepository<SoundImage, Long> {
}
