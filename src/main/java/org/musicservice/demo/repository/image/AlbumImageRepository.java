package org.musicservice.demo.repository.image;

import org.musicservice.demo.entity.image.AlbumImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlbumImageRepository extends JpaRepository<AlbumImage, Long> {

    Optional<AlbumImage> findByKey(String s3Key);

    AlbumImage findByAlbumId(Long albumId);
}
