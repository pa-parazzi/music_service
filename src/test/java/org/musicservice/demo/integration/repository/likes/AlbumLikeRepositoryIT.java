package org.musicservice.demo.integration.repository.likes;

import org.junit.jupiter.api.Test;
import org.musicservice.demo.entity.genre.Genre;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.musicservice.demo.support.pageable.AlbumAssertions.assertAlbumsWithArtistAndImage;
import static org.musicservice.demo.support.pageable.PageAssertions.*;

@DataJpaTest
public class AlbumLikeRepositoryIT extends AbstractIntegrationTest {

    @Autowired
    private AlbumLikeRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void deleteByUserIdAndAlbumId_ShouldDeleteRecord(){
        User user = entityManager.persist(UserDataFactoryIT.user());
        Album album = prepareAlbum();
        AlbumLike albumLike = entityManager.persist(MusicFactoryIT.albumLike(user, album));
        entityManager.flush();
        entityManager.clear();

        repository.deleteByUserIdAndAlbumId(user.getId(), album.getId());
        entityManager.flush();
        entityManager.clear();

        AlbumLike foundEntity = entityManager.find(AlbumLike.class, albumLike.getId());
        assertThat(foundEntity).isNull();
    }

    @Test
    void deleteByUserIdAndAlbumId_ShouldDoNothing_WhenUserIdIsIncorrectly(){
        User user = entityManager.persist(UserDataFactoryIT.user());
        Album album = prepareAlbum();
        AlbumLike albumLike = entityManager.persist(MusicFactoryIT.albumLike(user, album));

        entityManager.flush();
        entityManager.clear();

        repository.deleteByUserIdAndAlbumId(8710L, album.getId());
        entityManager.flush();
        entityManager.clear();

        AlbumLike foundEntity = entityManager.find(AlbumLike.class, albumLike.getId());
        assertThat(foundEntity).isNotNull();
    }

    @Test
    void deleteByUserIdAndAlbumId_ShouldDoNothing_WhenAlbumIdIsIncorrectly(){
        User user = entityManager.persist(UserDataFactoryIT.user());
        Album album = prepareAlbum();
        AlbumLike albumLike = entityManager.persist(MusicFactoryIT.albumLike(user, album));

        entityManager.flush();
        entityManager.clear();

        repository.deleteByUserIdAndAlbumId(user.getId(), 16352L);
        entityManager.flush();
        entityManager.clear();

        AlbumLike foundEntity = entityManager.find(AlbumLike.class, albumLike.getId());
        assertThat(foundEntity).isNotNull();
    }

    @Test
    void findByUserIdOrderByCreatedAtDesc_ShouldReturnsFirstPageCorrectlyWithOrderByCreatedAtDescIdDesc(){
        User user = entityManager.persist(UserDataFactoryIT.user());
        String titleAlbumPrefix = "bad romance";
        prepareAlbumLikes(user, titleAlbumPrefix);

        entityManager.flush();
        entityManager.clear();

        Page<AlbumLike> albumLikePage = repository.findByUserIdOrderByCreatedAtDescIdDesc(user.getId(), PageRequest.of(page, size));
        List<AlbumLike> albumLikeList = albumLikePage.getContent();
        List<Album> albums = albumLikeList.stream().map(AlbumLike::getAlbum).toList();

        assertAlbumLikesOrderByCreatedAtDesc(albumLikeList);
        assertAlbumsWithArtistAndImage(albums, titleAlbumPrefix);
        assertFirstPage(albumLikePage);
    }

    @Test
    void findByUserIdOrderByCreatedAtDesc_ShouldReturnsSecondPageCorrectlyWithOrderByCreatedAtDescIdDesc(){
        User user = entityManager.persist(UserDataFactoryIT.user());
        String titleAlbumPrefix = "bad romance";
        prepareAlbumLikes(user, titleAlbumPrefix);

        entityManager.flush();
        entityManager.clear();

        Page<AlbumLike> albumLikePage = repository.findByUserIdOrderByCreatedAtDescIdDesc(user.getId(), PageRequest.of(page + 1, size));
        List<AlbumLike> albumLikeList = albumLikePage.getContent();
        List<Album> albums = albumLikeList.stream().map(AlbumLike::getAlbum).toList();

        assertAlbumLikesOrderByCreatedAtDesc(albumLikeList);
        assertAlbumsWithArtistAndImage(albums, titleAlbumPrefix);
        assertSecondPage(albumLikePage);
    }

    @Test
    void findByUserIdOrderByCreatedAtDesc_ShouldReturnsLastPageCorrectlyWithOrderByCreatedAtDescIdDesc(){
        User user = entityManager.persist(UserDataFactoryIT.user());
        String titleAlbumPrefix = "bad romance";
        prepareAlbumLikes(user, titleAlbumPrefix);

        entityManager.flush();
        entityManager.clear();

        Page<AlbumLike> albumLikePage = repository.findByUserIdOrderByCreatedAtDescIdDesc(user.getId(), PageRequest.of(page + 2, size));
        List<AlbumLike> albumLikeList = albumLikePage.getContent();
        List<Album> albums = albumLikeList.stream().map(AlbumLike::getAlbum).toList();

        assertAlbumLikesOrderByCreatedAtDesc(albumLikeList);
        assertAlbumsWithArtistAndImage(albums, titleAlbumPrefix);
        assertLastPage(albumLikePage);
    }

    @Test
    void findByUserIdOrderByCreatedAtDesc_ShouldReturnsEmptyPage_WhenUserIdIsInvalid(){
        User user = entityManager.persist(UserDataFactoryIT.user());
        String titleAlbumPrefix = "bad romance";
        prepareAlbumLikes(user, titleAlbumPrefix);

        Page<AlbumLike> albumLikePage = repository.findByUserIdOrderByCreatedAtDescIdDesc(80923L, PageRequest.of(page, size));

        assertEmptyPage(albumLikePage);
    }

    @Test
    void existsByUserIdAndAlbumId_ShouldReturnTrue_WhenAlbumLikeIsExists(){
        User user = entityManager.persist(UserDataFactoryIT.user());
        Album album = prepareAlbum();
        entityManager.persist(MusicFactoryIT.albumLike(user, album));

        entityManager.flush();
        entityManager.clear();

        Boolean result = repository.existsByUserIdAndAlbumId(user.getId(), album.getId());
        assertThat(result).isTrue();
    }

    @Test
    void existsByUserIdAndAlbumId_ShouldReturnFalse_WhenAlbumIdIsInvalid(){
        User user = entityManager.persist(UserDataFactoryIT.user());
        Album album = prepareAlbum();
        entityManager.persist(MusicFactoryIT.albumLike(user, album));

        Boolean result = repository.existsByUserIdAndAlbumId(user.getId(), 8902L);
        assertThat(result).isFalse();
    }

    @Test
    void existsByUserIdAndAlbumId_ShouldReturnFalse_WhenUserIdIsInvalid(){
        User user = entityManager.persist(UserDataFactoryIT.user());
        Album album = prepareAlbum();
        entityManager.persist(MusicFactoryIT.albumLike(user, album));

        Boolean result = repository.existsByUserIdAndAlbumId(8919L, album.getId());
        assertThat(result).isFalse();
    }

    private void assertAlbumLikesOrderByCreatedAtDesc(List<AlbumLike> albumLikeList){
        List<Instant> createdAtAlbumLikesOrder = albumLikeList.stream().map(AlbumLike::getCreatedAt).toList();
        assertThat(createdAtAlbumLikesOrder).isSortedAccordingTo(Comparator.reverseOrder());
    }

    private void prepareAlbumLikes(User user, String titleAlbumPrefix){
        Instant createdAt = Instant.parse("2026-01-01T00:00:00Z");
        Genre genre = entityManager.persist(MusicFactoryIT.genre());
        Artist artist = entityManager.persist(MusicFactoryIT.artist(genre));
        for (int i = 0; i < totalElements; i++) {
            Album album = entityManager.persist
                    (new Album(titleAlbumPrefix + "_" + i, LocalDate.of(2003,6,10), artist, genre));
            AlbumLike albumLike = entityManager.persist(MusicFactoryIT.albumLike(user, album));
            albumLike.setCreatedAt(createdAt.plusSeconds(i));
        }
    }

    private Album prepareAlbum(){
        Genre genre = entityManager.persist(MusicFactoryIT.genre());
        Artist artist = entityManager.persist(MusicFactoryIT.artist(genre));
        return entityManager.persist(MusicFactoryIT.album(artist, genre));
    }
}
