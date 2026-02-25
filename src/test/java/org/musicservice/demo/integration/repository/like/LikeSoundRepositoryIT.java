package org.musicservice.demo.integration.repository.like;

import org.junit.jupiter.api.Test;
import org.musicservice.demo.entity.like.LikeSound;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.entity.music.Sound;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.repository.like.LikeSoundRepository;
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
public class LikeSoundRepositoryIT extends AbstractIntegrationTest {

    @Autowired
    private LikeSoundRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void deleteByUserIdAndAlbumId_ShouldDeleteRecord(){
        User user = entityManager.persistAndFlush(UserDataFactoryIT.user());
        Artist artist = entityManager.persistAndFlush(MusicFactoryIT.artist());
        Album album = entityManager.persistAndFlush(MusicFactoryIT.album(artist));
        Sound sound = entityManager.persistAndFlush(MusicFactoryIT.sound(artist, album));
        LikeSound likeSound = entityManager.persistAndFlush(MusicFactoryIT.likeSound(user, sound));
        entityManager.clear();

        repository.deleteByUserIdAndSoundId(user.getId(), sound.getId());
        entityManager.flush();
        entityManager.clear();

        LikeSound foundEntity = entityManager.find(LikeSound.class, likeSound.getId());
        assertThat(foundEntity).isNull();
    }

    @Test
    void findAllByUserIdOrderByCreatedAtDesc_ShouldReturnValidSortedListByDesc() {
        User user = entityManager.persistAndFlush(UserDataFactoryIT.user());
        Artist artist = entityManager.persistAndFlush(MusicFactoryIT.artist());
        Album album = entityManager.persistAndFlush(MusicFactoryIT.album(artist));
        List<Sound> soundList = MusicFactoryIT.soundList(artist, album);
        soundList.forEach(sound -> entityManager.persistAndFlush(sound));
        entityManager.persistAndFlush(MusicFactoryIT.likeSound(user, soundList.get(2)));
        entityManager.persistAndFlush(MusicFactoryIT.likeSound(user, soundList.get(0)));
        entityManager.persistAndFlush(MusicFactoryIT.likeSound(user, soundList.get(1)));
        entityManager.clear();

        List<LikeSound> actualOrder = repository.findAllByUserIdOrderByCreatedAtDesc(user.getId());
        assertThat(actualOrder).extracting(LikeSound::getCreatedAt).isSortedAccordingTo(Comparator.reverseOrder());
    }

    @Test
    void findAllByUserIdOrderByCreatedAtDesc_ShouldReturnEmptyList(){
        assertThat(repository.findAllByUserIdOrderByCreatedAtDesc(15L)).isEmpty();
    }
}
