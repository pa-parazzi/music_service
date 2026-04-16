package org.musicservice.demo.integration.repository.likes;

import org.junit.jupiter.api.Test;
import org.musicservice.demo.entity.genre.Genre;
import org.musicservice.demo.entity.genre.GenreName;
import org.musicservice.demo.entity.likes.AlbumLike;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.repository.likes.AlbumLikeRepository;
import org.musicservice.demo.repository.music.GenreRepository;
import org.musicservice.demo.support.config.AbstractJpaIT;
import org.musicservice.demo.support.factory.it.user.UserDataFactoryIT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.musicservice.demo.support.assertions.AlbumAssertions.assertAlbumsWithArtistAndImage;
import static org.musicservice.demo.support.assertions.PageAssertions.*;
import static org.musicservice.demo.support.fixture.jpa.AlbumJpaFixture.albumAggregateWithAlbums;
import static org.musicservice.demo.support.fixture.jpa.AlbumJpaFixture.albumAggregateWithOneAlbum;
import static org.musicservice.demo.support.fixture.jpa.AlbumLikeJpaFixture.createAlbumLikes;

public class AlbumLikeRepositoryIT extends AbstractJpaIT {

    @Autowired
    private AlbumLikeRepository repository;
    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Genre findGenre(){
        return genreRepository.findByName(GenreName.ROCK).orElseThrow();
    }

    @Test
    void deleteByUserIdAndAlbumId_ShouldDeleteRecord(){
        Genre genre = findGenre();
        User user = entityManager.persist(UserDataFactoryIT.user());
        Album album = albumAggregateWithOneAlbum(genre, entityManager).albums().getFirst();
        createAlbumLikes(entityManager, user, List.of(album));
        entityManager.flush();
        entityManager.clear();

        repository.deleteByUserIdAndAlbumId(user.getId(), album.getId());
        entityManager.flush();
        entityManager.clear();

        List<AlbumLike> albumLikes = repository.findAll();
        assertThat(albumLikes).isEmpty();
    }

    @Test
    void deleteByUserIdAndAlbumId_ShouldDoNothing_WhenUserIdIsIncorrectly(){
        Genre genre = findGenre();
        User user = entityManager.persist(UserDataFactoryIT.user());
        Album album = albumAggregateWithOneAlbum(genre, entityManager).albums().getFirst();
        createAlbumLikes(entityManager, user, List.of(album));

        entityManager.flush();
        entityManager.clear();

        repository.deleteByUserIdAndAlbumId(8710L, album.getId());
        entityManager.flush();
        entityManager.clear();

        List<AlbumLike> albumLikes = repository.findAll();
        assertThat(albumLikes).isNotEmpty();
    }

    @Test
    void deleteByUserIdAndAlbumId_ShouldDoNothing_WhenAlbumIdIsIncorrectly(){
        Genre genre = findGenre();
        User user = entityManager.persist(UserDataFactoryIT.user());
        Album album = albumAggregateWithOneAlbum(genre, entityManager).albums().getFirst();
        createAlbumLikes(entityManager, user, List.of(album));

        entityManager.flush();
        entityManager.clear();

        repository.deleteByUserIdAndAlbumId(user.getId(), 16352L);
        entityManager.flush();
        entityManager.clear();

        List<AlbumLike> albumLikes = repository.findAll();
        assertThat(albumLikes).isNotEmpty();
    }

    @Test
    void findByUserIdOrderByCreatedAtDesc_ShouldReturnsFirstPageCorrectlyWithOrderByCreatedAtDescIdDesc(){
        Genre genre = findGenre();
        User user = entityManager.persist(UserDataFactoryIT.user());
        String titleAlbumPrefix = "bad romance";
        List<Album> albums = albumAggregateWithAlbums(genre, entityManager, titleAlbumPrefix).albums();
        createAlbumLikes(entityManager, user, albums);

        entityManager.flush();
        entityManager.clear();

        Page<AlbumLike> albumLikePage = repository.findByUserIdOrderByCreatedAtDescIdDesc(user.getId(), PageRequest.of(page, size));
        List<AlbumLike> albumLikeList = albumLikePage.getContent();
        List<Album> likedAlbums = albumLikeList.stream().map(AlbumLike::getAlbum).toList();

        assertAlbumLikesOrderByCreatedAtDesc(albumLikeList);
        assertAlbumsWithArtistAndImage(likedAlbums, titleAlbumPrefix);
        assertFirstPage(albumLikePage);
    }

    @Test
    void findByUserIdOrderByCreatedAtDesc_ShouldReturnsSecondPageCorrectlyWithOrderByCreatedAtDescIdDesc(){
        Genre genre = findGenre();
        User user = entityManager.persist(UserDataFactoryIT.user());
        String titleAlbumPrefix = "bad romance";
        List<Album> albums = albumAggregateWithAlbums(genre, entityManager, titleAlbumPrefix).albums();
        createAlbumLikes(entityManager, user, albums);

        entityManager.flush();
        entityManager.clear();

        Page<AlbumLike> albumLikePage = repository.findByUserIdOrderByCreatedAtDescIdDesc(user.getId(), PageRequest.of(page + 1, size));
        List<AlbumLike> albumLikeList = albumLikePage.getContent();
        List<Album> likedAlbums = albumLikeList.stream().map(AlbumLike::getAlbum).toList();

        assertAlbumLikesOrderByCreatedAtDesc(albumLikeList);
        assertAlbumsWithArtistAndImage(likedAlbums, titleAlbumPrefix);
        assertSecondPage(albumLikePage);
    }

    @Test
    void findByUserIdOrderByCreatedAtDesc_ShouldReturnsLastPageCorrectlyWithOrderByCreatedAtDescIdDesc(){
        Genre genre = findGenre();
        User user = entityManager.persist(UserDataFactoryIT.user());
        String titleAlbumPrefix = "bad romance";
        List<Album> albums = albumAggregateWithAlbums(genre, entityManager, titleAlbumPrefix).albums();
        createAlbumLikes(entityManager, user, albums);

        entityManager.flush();
        entityManager.clear();

        Page<AlbumLike> albumLikePage = repository.findByUserIdOrderByCreatedAtDescIdDesc(user.getId(), PageRequest.of(page + 2, size));
        List<AlbumLike> albumLikeList = albumLikePage.getContent();
        List<Album> likedAlbums = albumLikeList.stream().map(AlbumLike::getAlbum).toList();

        assertAlbumLikesOrderByCreatedAtDesc(albumLikeList);
        assertAlbumsWithArtistAndImage(likedAlbums, titleAlbumPrefix);
        assertLastPage(albumLikePage);
    }

    @Test
    void findByUserIdOrderByCreatedAtDesc_ShouldReturnsEmptyPage_WhenUserIdIsInvalid(){
        Page<AlbumLike> albumLikePage = repository
                .findByUserIdOrderByCreatedAtDescIdDesc(80923L, PageRequest.of(page, size));
        assertEmptyPage(albumLikePage);
    }

    @Test
    void existsByUserIdAndAlbumId_ShouldReturnTrue_WhenAlbumLikeIsExists(){
        Genre genre = findGenre();
        User user = entityManager.persist(UserDataFactoryIT.user());
        Album album = albumAggregateWithOneAlbum(genre, entityManager).albums().getFirst();
        createAlbumLikes(entityManager, user, List.of(album));

        entityManager.flush();
        entityManager.clear();

        Boolean result = repository.existsByUserIdAndAlbumId(user.getId(), album.getId());
        assertThat(result).isTrue();
    }

    @Test
    void existsByUserIdAndAlbumId_ShouldReturnFalse_WhenAlbumIdIsInvalid(){
        Genre genre = findGenre();
        User user = entityManager.persist(UserDataFactoryIT.user());
        Album album = albumAggregateWithOneAlbum(genre, entityManager).albums().getFirst();
        createAlbumLikes(entityManager, user, List.of(album));

        entityManager.flush();
        entityManager.clear();

        Boolean result = repository.existsByUserIdAndAlbumId(user.getId(), 8902L);
        assertThat(result).isFalse();
    }

    @Test
    void existsByUserIdAndAlbumId_ShouldReturnFalse_WhenUserIdIsInvalid(){
        Genre genre = findGenre();
        User user = entityManager.persist(UserDataFactoryIT.user());
        Album album = albumAggregateWithOneAlbum(genre, entityManager).albums().getFirst();
        createAlbumLikes(entityManager, user, List.of(album));

        entityManager.flush();
        entityManager.clear();

        Boolean result = repository.existsByUserIdAndAlbumId(8919L, album.getId());
        assertThat(result).isFalse();
    }

    private void assertAlbumLikesOrderByCreatedAtDesc(List<AlbumLike> albumLikes){
        List<Instant> createdAtAlbumLikesOrder = albumLikes.stream().map(AlbumLike::getCreatedAt).toList();
        assertThat(createdAtAlbumLikesOrder).isSortedAccordingTo(Comparator.reverseOrder());
    }
}
