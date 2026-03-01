package org.musicservice.demo.integration.repository.likes;

import org.junit.jupiter.api.Test;
import org.musicservice.demo.entity.likes.SoundLike;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.entity.music.Sound;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.repository.likes.SoundLikeRepository;
import org.musicservice.demo.support.config.AbstractIntegrationTest;
import org.musicservice.demo.support.factory.it.music.MusicFactoryIT;
import org.musicservice.demo.support.factory.it.user.UserDataFactoryIT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class SoundLikeRepositoryIT extends AbstractIntegrationTest {

    @Autowired
    private SoundLikeRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void deleteByUserIdAndAlbumId_ShouldDeleteRecord(){
        User user = entityManager.persistAndFlush(UserDataFactoryIT.user());
        Artist artist = entityManager.persistAndFlush(MusicFactoryIT.artist());
        Album album = entityManager.persistAndFlush(MusicFactoryIT.album(artist));
        Sound sound = entityManager.persistAndFlush(MusicFactoryIT.sound(artist, album));
        SoundLike soundLike = entityManager.persistAndFlush(MusicFactoryIT.soundLike(user, sound));
        entityManager.clear();

        repository.deleteByUserIdAndSoundId(user.getId(), sound.getId());
        entityManager.flush();
        entityManager.clear();

        SoundLike foundEntity = entityManager.find(SoundLike.class, soundLike.getId());
        assertThat(foundEntity).isNull();
    }

    @Test
    void findAllByUserIdOrderByCreatedAtDesc_ShouldReturnsValidSortedListByDesc() {
        User user = entityManager.persistAndFlush(UserDataFactoryIT.user());
        Artist artist = entityManager.persistAndFlush(MusicFactoryIT.artist());
        Album album = entityManager.persistAndFlush(MusicFactoryIT.album(artist));
        List<Sound> soundList = MusicFactoryIT.soundList(artist, album);
        soundList.forEach(sound -> entityManager.persistAndFlush(sound));
        entityManager.persistAndFlush(MusicFactoryIT.soundLike(user, soundList.get(2)));
        entityManager.persistAndFlush(MusicFactoryIT.soundLike(user, soundList.get(0)));
        entityManager.persistAndFlush(MusicFactoryIT.soundLike(user, soundList.get(1)));
        entityManager.clear();

        List<SoundLike> actualOrder = repository.findAllByUserIdOrderByCreatedAtDesc(user.getId());
        assertThat(actualOrder).extracting(SoundLike::getCreatedAt).isSortedAccordingTo(Comparator.reverseOrder());
    }

    @Test
    void findAllByUserIdOrderByCreatedAtDesc_ShouldReturnsEmptyList(){
        assertThat(repository.findAllByUserIdOrderByCreatedAtDesc(15L)).isEmpty();
    }
}
