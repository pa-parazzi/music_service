package org.musicservice.demo.integration.repository.music;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.junit.jupiter.api.Test;
import org.musicservice.demo.entity.genre.Genre;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.musicservice.demo.support.config.AbstractIntegrationTest;
import org.musicservice.demo.support.factory.it.music.MusicFactoryIT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.musicservice.demo.support.assertions.AlbumAssertions.assertAlbumsWithArtistAndImage;
import static org.musicservice.demo.support.assertions.PageAssertions.*;
import static org.musicservice.demo.support.factory.it.music.AlbumFactoryIT.prepareAlbumWithAllRelations;
import static org.musicservice.demo.support.factory.it.music.AlbumFactoryIT.prepareAlbumsWithAllRelations;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AlbumRepositoryIT extends AbstractIntegrationTest {

    @Autowired
    private AlbumRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByIdWithArtistAndImage_ShouldReturnsAlbumWithArtistAndImage(){
        Album expectedAlbum = prepareAlbumWithAllRelations(entityManager);

        entityManager.flush();
        entityManager.clear();

        Album actualAlbum = repository.findByIdWithArtistAndImage(expectedAlbum.getId()).orElseThrow();
        assertThat(actualAlbum.getId()).isEqualTo(expectedAlbum.getId());
        assertThat(actualAlbum.getTitle()).isEqualTo(expectedAlbum.getTitle());
        assertThat(actualAlbum.getReleaseDate()).isEqualTo(expectedAlbum.getReleaseDate());

        assertThat(actualAlbum.getArtist()).isNotNull().isNotInstanceOf(HibernateProxy.class);
        assertThat(Hibernate.isInitialized(actualAlbum.getArtist())).isTrue();

        assertThat(actualAlbum.getImage()).isNotNull().isNotInstanceOf(HibernateProxy.class);
        assertThat(Hibernate.isInitialized(actualAlbum.getImage())).isTrue();
    }

    @Test
    void findByIdWithArtistAndImage_ShouldReturnsEmpty_WhenIdIsInvalid(){
        prepareAlbumWithAllRelations(entityManager);

        entityManager.flush();
        entityManager.clear();

        assertThat(repository.findByIdWithArtistAndImage(156L)).isEmpty();
    }

    @Test
    void findByTitleStartingWithIgnoreCase_ShouldReturnsFirstPageCorrectly(){
        String albumTitlePrefix = "bad romance";
        Genre genre = entityManager.persist(MusicFactoryIT.genre());
        Artist artist = entityManager.persist(MusicFactoryIT.artist(genre));
        prepareAlbumsWithAllRelations(entityManager, genre, artist, albumTitlePrefix);

        entityManager.flush();
        entityManager.clear();

        Page<Album> albumPage = repository.findByTitleStartingWithIgnoreCase
                (albumTitlePrefix, PageRequest.of(page, size));
        List<Album> albums = albumPage.getContent();

        assertAlbumsWithArtistAndImage(albums, albumTitlePrefix);
        assertFirstPage(albumPage);
    }

    @Test
    void findByTitleStartingWithIgnoreCase_ShouldReturnsSecondPageCorrectly(){
        String albumTitlePrefix = "just dance";
        Genre genre = entityManager.persist(MusicFactoryIT.genre());
        Artist artist = entityManager.persist(MusicFactoryIT.artist(genre));
        prepareAlbumsWithAllRelations(entityManager, genre, artist, albumTitlePrefix);

        entityManager.flush();
        entityManager.clear();

        Page<Album> albumPage = repository.findByTitleStartingWithIgnoreCase
                        (albumTitlePrefix, PageRequest.of(page + 1, size));
        List<Album> albums = albumPage.getContent();

        assertAlbumsWithArtistAndImage(albums, albumTitlePrefix);
        assertSecondPage(albumPage);
    }

    @Test
    void findByTitleStartingWithIgnoreCase_ShouldReturnsLastPageCorrectly(){
        String albumTitlePrefix = "after dark";
        Genre genre = entityManager.persist(MusicFactoryIT.genre());
        Artist artist = entityManager.persist(MusicFactoryIT.artist(genre));
        prepareAlbumsWithAllRelations(entityManager, genre, artist, albumTitlePrefix);

        entityManager.flush();
        entityManager.clear();

        Page<Album> albumPage = repository.findByTitleStartingWithIgnoreCase
                (albumTitlePrefix, PageRequest.of(page + 2, size));
        List<Album> albums = albumPage.getContent();

        assertAlbumsWithArtistAndImage(albums, albumTitlePrefix);
        assertLastPage(albumPage);
    }

    @Test
    void findByTitleStartingWithIgnoreCase_ShouldReturnsEmptyPage_WhenAlbumsNotFoundByPrefix(){
        Genre genre = entityManager.persist(MusicFactoryIT.genre());
        Artist artist = entityManager.persist(MusicFactoryIT.artist(genre));
        prepareAlbumsWithAllRelations(entityManager, genre, artist,"some album");

        entityManager.flush();
        entityManager.clear();

        Page<Album> result = repository.findByTitleStartingWithIgnoreCase
                ("incorrectly prefix", PageRequest.of(page, size));

        assertEmptyPage(result);
    }

    @Test
    void findByGenreId_ShouldReturnsFirstPageCorrectly(){
        Genre genre = entityManager.persist(MusicFactoryIT.genre());
        Artist artist = entityManager.persist(MusicFactoryIT.artist(genre));
        String albumTitlePrefix = "supermassive";
        prepareAlbumsWithAllRelations(entityManager, genre, artist, albumTitlePrefix);

        entityManager.flush();
        entityManager.clear();

        Page<Album> albumPage = repository.findByGenreId(genre.getId(), PageRequest.of(page, size));
        List<Album> albums = albumPage.getContent();

        assertAlbumsWithArtistAndImage(albums, albumTitlePrefix);
        assertFirstPage(albumPage);
    }

    @Test
    void findByGenreId_ShouldReturnsSecondPageCorrectly(){
        Genre genre = entityManager.persist(MusicFactoryIT.genre());
        Artist artist = entityManager.persist(MusicFactoryIT.artist(genre));
        String albumTitlePrefix = "just dance";
        prepareAlbumsWithAllRelations(entityManager, genre, artist, albumTitlePrefix);

        entityManager.flush();
        entityManager.clear();

        Page<Album> albumPage = repository.findByGenreId(genre.getId(), PageRequest.of(page + 1, size));
        List<Album> albums = albumPage.getContent();

        assertAlbumsWithArtistAndImage(albums, albumTitlePrefix);
        assertSecondPage(albumPage);
    }

    @Test
    void findByGenreId_ShouldReturnsLastPageCorrectly(){
        Genre genre = entityManager.persist(MusicFactoryIT.genre());
        Artist artist = entityManager.persist(MusicFactoryIT.artist(genre));
        String albumTitlePrefix = "just";
        prepareAlbumsWithAllRelations(entityManager, genre, artist, albumTitlePrefix);
        entityManager.flush();
        entityManager.clear();

        Page<Album> albumPage = repository.findByGenreId(genre.getId(), PageRequest.of(page + 2, size));
        List<Album> albums = albumPage.getContent();

        assertAlbumsWithArtistAndImage(albums, albumTitlePrefix);
        assertLastPage(albumPage);
    }

    @Test
    void findByGenreId_ShouldReturnsEmptyPage_WhenGenreIdIsInvalid(){
        Page<Album> albumPage = repository.findByGenreId(893234L, PageRequest.of(page + 2, size));

        assertEmptyPage(albumPage);
    }

}
