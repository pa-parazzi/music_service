package org.musicservice.demo.integration.repository.music;

import org.junit.jupiter.api.Test;
import org.musicservice.demo.entity.genre.Genre;
import org.musicservice.demo.entity.genre.GenreName;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.repository.music.ArtistRepository;
import org.musicservice.demo.repository.music.GenreRepository;
import org.musicservice.demo.support.config.AbstractJpaIT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.musicservice.demo.support.assertions.ArtistAssertions.assertArtists;
import static org.musicservice.demo.support.assertions.PageAssertions.*;
import static org.musicservice.demo.support.fixture.jpa.ArtistJpaFixture.createArtists;

public class ArtistRepositoryIT extends AbstractJpaIT {

    @Autowired
    private ArtistRepository repository;
    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Genre findGenre(){
        return genreRepository.findByName(GenreName.ROCK).orElseThrow();
    }

    @Test
    void findByNameStartingWithIgnoreCase_ShouldReturnsFirstPageCorrectly() {
        Genre genre = findGenre();
        String artisNamePrefix = "Lady Gaga";
        createArtists(genre, entityManager, artisNamePrefix);

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
        Genre genre = findGenre();
        String artisNamePrefix = "Lady Gaga";
        createArtists(genre, entityManager, artisNamePrefix);

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
        Genre genre = findGenre();
        String artisNamePrefix = "Lady Gaga";
        createArtists(genre, entityManager, artisNamePrefix);

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
}
