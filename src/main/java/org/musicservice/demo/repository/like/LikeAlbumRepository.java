package org.musicservice.demo.repository.like;

import org.musicservice.demo.model.like.LikeAlbum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeAlbumRepository extends JpaRepository<LikeAlbum, Long> {

    @Modifying
    @Query("delete from LikeAlbum l where l.user.id = :userId and l.album.id = :albumId")
    void deleteByUserIdAndAlbumId(Long userId, Long albumId);

    List<LikeAlbum> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}
