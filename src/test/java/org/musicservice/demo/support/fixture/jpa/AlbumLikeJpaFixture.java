package org.musicservice.demo.support.fixture.jpa;

import org.musicservice.demo.entity.likes.AlbumLike;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.support.factory.it.music.MusicFactoryIT;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.Instant;
import java.util.List;

public class AlbumLikeJpaFixture {

    public static void createAlbumLikes(TestEntityManager entityManager, User user, List<Album> albums){
        Instant createdAt = Instant.parse("2026-01-01T00:00:00Z");
        int second = 1;
        for (Album album : albums) {
            AlbumLike albumLike = entityManager.persist(MusicFactoryIT.albumLike(user, album));
            albumLike.setCreatedAt(createdAt.plusSeconds(second++));
        }
    }

}
