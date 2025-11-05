package org.musicservice.demo.repository.image;

import org.musicservice.demo.model.image.AlbumImage;
import org.musicservice.demo.model.music.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlbumImageRepository extends JpaRepository<AlbumImage, Long> {

    Optional<AlbumImage> findByS3Key(String s3Key);

    AlbumImage findByAlbumId(Long albumId);
}
