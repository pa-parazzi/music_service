package org.musicservice.demo.support.fixture.jpa;

import org.musicservice.demo.entity.likes.SoundLike;
import org.musicservice.demo.entity.music.Sound;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.support.factory.it.music.MusicFactoryIT;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.Instant;
import java.util.List;

public class SoundLikeJpaFixture {

    public static void createSoundLikes(TestEntityManager entityManager, User user, List<Sound> sounds) {
        Instant createdAt = Instant.parse("2026-01-01T00:00:00Z");
        int second = 1;
        for (Sound sound : sounds) {
            SoundLike soundLike = entityManager.persist(MusicFactoryIT.soundLike(user, sound));
            soundLike.setCreatedAt(createdAt.plusSeconds(second++));
        }
    }
}
