package org.musicservice.demo.repository.likes;

import org.musicservice.demo.entity.likes.AlbumLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumLikeRepository extends JpaRepository<AlbumLike, Long> {

    void deleteByUserIdAndAlbumId(Long userId, Long albumId);

    @EntityGraph(attributePaths = {"album.image", "album.artist"})
    Page<AlbumLike> findByUserIdOrderByCreatedAtDescIdDesc(Long userId, Pageable pageable);

    Boolean existsByUserIdAndAlbumId(Long userId, Long albumId);
}
