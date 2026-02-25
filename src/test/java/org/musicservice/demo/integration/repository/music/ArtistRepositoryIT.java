package org.musicservice.demo.integration.repository.music;

import org.junit.jupiter.api.Test;
import org.musicservice.demo.dto.music.artist.ArtistResponse;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.repository.music.ArtistRepository;
import org.musicservice.demo.support.config.AbstractIntegrationTest;
import org.musicservice.demo.support.factory.it.music.MusicFactoryIT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@DataJpaTest
public class ArtistRepositoryIT extends AbstractIntegrationTest {

    @Autowired
    private ArtistRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findAllArtistResponseByNameStartingWith_ShouldReturnsListArtistResponse_WhenFragmentIsCorrect() {
        Artist artist = entityManager.persistAndFlush(MusicFactoryIT.artist2());
        Artist artist2 = entityManager.persistAndFlush(MusicFactoryIT.artist3());
        entityManager.clear();
        String fragment = "T";

        List<ArtistResponse> actualResponseList = repository.findAllArtistResponseByNameStartingWith(fragment);
        assertThat(actualResponseList)
                .extracting(ArtistResponse::id, ArtistResponse::name)
                .containsExactlyInAnyOrder(
                        tuple(artist.getId(), artist.getName()),
                        tuple(artist2.getId(), artist2.getName())
                );
    }

    @Test
    void findAllArtistResponseByNameStartingWith_ShouldReturnsEmptyList_WhenFragmentIsIncorrect(){
        entityManager.persistAndFlush(MusicFactoryIT.artist());
        entityManager.persistAndFlush(MusicFactoryIT.artist2());
        entityManager.clear();
        String fragment = "incorrect fragment";

        List<ArtistResponse> actualResponseList = repository.findAllArtistResponseByNameStartingWith(fragment);
        assertThat(actualResponseList).isEmpty();
    }

    @Test
    void findAllArtistResponseByNameStartingWith_ShouldReturnsEmptyList_WhenFragmentIsEmpty(){
        entityManager.persistAndFlush(MusicFactoryIT.artist());
        entityManager.persistAndFlush(MusicFactoryIT.artist2());
        entityManager.clear();
        String fragment = " ";

        List<ArtistResponse> actualResponseList = repository.findAllArtistResponseByNameStartingWith(fragment);
        assertThat(actualResponseList).isEmpty();
    }

    @Test
    void findAllArtistResponseByNameStartingWith_ShouldReturnsEmptyList_WhenFragmentIsNull(){
        entityManager.persistAndFlush(MusicFactoryIT.artist());
        entityManager.persistAndFlush(MusicFactoryIT.artist2());
        entityManager.clear();
        String fragment = null;

        List<ArtistResponse> actualResponseList = repository.findAllArtistResponseByNameStartingWith(fragment);
        assertThat(actualResponseList).isEmpty();
    }

    @Test
    void findArtistResponseById_ShouldReturnArtistResponse(){
        Artist artist = entityManager.persistAndFlush(MusicFactoryIT.artist());
        entityManager.clear();

        ArtistResponse actualResponse = repository.findArtistResponseById(artist.getId()).orElseThrow();
        assertThat(actualResponse.id()).isEqualTo(artist.getId());
        assertThat(actualResponse.name()).isEqualTo(artist.getName());
    }

    @Test
    void findArtistResponseById_ShouldReturnEmpty_WhenIdIsIncorrect(){
        entityManager.persistAndFlush(MusicFactoryIT.artist());
        entityManager.clear();

        assertThat(repository.findArtistResponseById(235L)).isEmpty();
    }

    @Test
    void findArtistResponseById_ShouldReturnEmpty_WhenIdIsNull(){
        entityManager.persistAndFlush(MusicFactoryIT.artist());
        entityManager.clear();

        assertThat(repository.findArtistResponseById(null)).isEmpty();
    }
}
