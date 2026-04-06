package org.musicservice.demo.integration.repository.music;

import org.junit.jupiter.api.Test;
import org.musicservice.demo.entity.genre.Genre;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.repository.music.ArtistRepository;
import org.musicservice.demo.support.config.AbstractIntegrationTest;
import org.musicservice.demo.support.factory.it.music.MusicFactoryIT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.musicservice.demo.support.assertions.PageAssertions.*;

@DataJpaTest
public class ArtistRepositoryIT extends AbstractIntegrationTest {

    @Autowired
    private ArtistRepository repository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByNameStartingWithIgnoreCase_ShouldReturnsFirstPageCorrectly() {
        Genre genre = entityManager.persist(MusicFactoryIT.genre());
        String artisNamePrefix = "Lady Gaga";
        prepareArtists(genre, artisNamePrefix);

        entityManager.flush();
        entityManager.clear();

        Page<Artist> artistPage = repository
                .findByNameStartingWithIgnoreCase(artisNamePrefix, PageRequest.of(page, size));
        List<Artist> artists = artistPage.getContent();

        assertArtists(artists, artisNamePrefix);
        assertFirstPage(artistPage);
    }

    @Test
    void findByNameStartingWithIgnoreCase_ShouldReturnsSecondPageCorrectly(){
        Genre genre = entityManager.persist(MusicFactoryIT.genre());
        String artisNamePrefix = "Lady Gaga";
        prepareArtists(genre, artisNamePrefix);

        entityManager.flush();
        entityManager.clear();

        Page<Artist> artistPage = repository
                .findByNameStartingWithIgnoreCase(artisNamePrefix, PageRequest.of(page + 1, size));
        List<Artist> artists = artistPage.getContent();

        assertArtists(artists, artisNamePrefix);
        assertSecondPage(artistPage);
    }

    @Test
    void findByNameStartingWithIgnoreCase_ShouldReturnsLastPageCorrectly(){
        Genre genre = entityManager.persist(MusicFactoryIT.genre());
        String artisNamePrefix = "Lady Gaga";
        prepareArtists(genre, artisNamePrefix);

        entityManager.flush();
        entityManager.clear();

        Page<Artist> artistPage = repository
                .findByNameStartingWithIgnoreCase(artisNamePrefix, PageRequest.of(page + 2, size));
        List<Artist> artists = artistPage.getContent();

        assertArtists(artists, artisNamePrefix);
        assertLastPage(artistPage);
    }

    @Test
    void findByNameStartingWithIgnoreCase_ShouldReturnsEmptyPage_WhenArtistNotFoundByPrefix(){
        Page<Artist> artistPage = repository
                .findByNameStartingWithIgnoreCase("incorrect artist prefix", PageRequest.of(page, size));

        assertEmptyPage(artistPage);
    }

    private void prepareArtists(Genre genre, String artistNamePrefix){
        for (int i = 0; i < totalElements; i++) {
            entityManager.persist(new Artist(artistNamePrefix + "_" + i, genre));
        }
    }

    private void assertArtists(List<Artist> artists, String artistNamePrefix){
        assertThat(artists).extracting(Artist::getId).isNotEmpty();
        assertThat(artists).extracting(Artist::getName).allMatch(name -> name.startsWith(artistNamePrefix));
    }
}
