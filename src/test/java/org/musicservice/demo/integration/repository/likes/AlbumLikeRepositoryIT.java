package org.musicservice.demo.integration.repository.likes;

import org.junit.jupiter.api.Test;
import org.musicservice.demo.entity.likes.AlbumLike;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.repository.likes.AlbumLikeRepository;
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
public class AlbumLikeRepositoryIT extends AbstractIntegrationTest {

    @Autowired
    private AlbumLikeRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void deleteByUserIdAndAlbumId_ShouldDeleteRecord(){
        // Регистрация объектов в текущем контексте управления сущностями
        User user = entityManager.persistAndFlush(UserDataFactoryIT.user());
        Artist artist = entityManager.persistAndFlush(MusicFactoryIT.artist());
        Album album = entityManager.persistAndFlush(MusicFactoryIT.album(artist));
        AlbumLike albumLike = entityManager.persistAndFlush(MusicFactoryIT.albumLike(user, album));
        entityManager.clear();

        // Вызов тестируемого метода репозитория
        repository.deleteByUserIdAndAlbumId(user.getId(), album.getId());
        entityManager.flush();
        entityManager.clear();

        // Сравнение ожидаемого результата с реальным поведением
        AlbumLike foundEntity = entityManager.find(AlbumLike.class, albumLike.getId());
        assertThat(foundEntity).isNull();
    }

    @Test
    void findAllByUserIdOrderByCreatedAtDesc_ShouldReturnsValidSortedListByDesc() {
        User user = entityManager.persistAndFlush(UserDataFactoryIT.user());
        Artist artist = entityManager.persistAndFlush(MusicFactoryIT.artist());
        Album album = entityManager.persistAndFlush(MusicFactoryIT.album(artist));
        Album album2 = entityManager.persistAndFlush(MusicFactoryIT.album2(artist));
        Album album3 = entityManager.persistAndFlush(MusicFactoryIT.album3(artist));

        entityManager.persistAndFlush(MusicFactoryIT.albumLike(user, album3));
        entityManager.persistAndFlush(MusicFactoryIT.albumLike(user, album));
        entityManager.persistAndFlush(MusicFactoryIT.albumLike(user, album2));
        entityManager.clear();

        List<AlbumLike> actualOrder = repository.findAllByUserIdOrderByCreatedAtDesc(user.getId());
        assertThat(actualOrder).extracting(AlbumLike::getCreatedAt).isSortedAccordingTo(Comparator.reverseOrder());
    }

    @Test
    void findAllByUserIdOrderByCreatedAtDesc_ShouldReturnsEmptyList(){
        assertThat(repository.findAllByUserIdOrderByCreatedAtDesc(15L)).isEmpty();
    }
}
