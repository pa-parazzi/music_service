package org.musicservice.demo.repository.likes;

import org.musicservice.demo.entity.likes.AlbumLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumLikeRepository extends JpaRepository<AlbumLike, Long> {

    @Modifying
    @Query("delete from AlbumLike l where l.user.id = :userId and l.album.id = :albumId")
    void deleteByUserIdAndAlbumId(Long userId, Long albumId);

    List<AlbumLike> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}
