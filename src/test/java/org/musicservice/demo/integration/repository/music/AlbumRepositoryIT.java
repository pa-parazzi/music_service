package org.musicservice.demo.integration.repository.music;

import org.junit.jupiter.api.Test;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.musicservice.demo.support.config.AbstractIntegrationTest;
import org.musicservice.demo.support.factory.it.music.MusicFactoryIT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@DataJpaTest
public class AlbumRepositoryIT extends AbstractIntegrationTest {

    @Autowired
    private AlbumRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findAllByTitleStartingWith_ShouldReturnsValidAlbumList_WhenFragmentIsCorrect(){
        Artist artist = entityManager.persistAndFlush(MusicFactoryIT.artist());
        Album album = entityManager.persistAndFlush(MusicFactoryIT.album3(artist));
        Album album2 = entityManager.persistAndFlush(MusicFactoryIT.album4(artist));
        album.setImage(entityManager.persistAndFlush(MusicFactoryIT.albumImage(album)));
        album2.setImage(entityManager.persistAndFlush(MusicFactoryIT.albumImage(album2)));
        entityManager.clear();

        String fragment = "A";
        List<Album> result = repository.findAllByTitleStartingWith(fragment);
        assertAlbumList(result, album, album2);
    }

    @Test
    void findAllByTitleStartingWith_ShouldReturnsValidAlbumList_WhenFragmentIsIncorrect(){
        Artist artist = entityManager.persistAndFlush(MusicFactoryIT.artist());
        Album album = entityManager.persistAndFlush(MusicFactoryIT.album(artist));
        album.setImage(entityManager.persistAndFlush(MusicFactoryIT.albumImage(album)));
        entityManager.clear();

        String fragmentTitle = "incorrect";
        List<Album> result = repository.findAllByTitleStartingWith(fragmentTitle);
        assertThat(result).isEmpty();
    }

    @Test
    void findAllForMainPage_ShouldReturnsValidAlbumList(){
        Artist artist = entityManager.persistAndFlush(MusicFactoryIT.artist());
        Album album = entityManager.persistAndFlush(MusicFactoryIT.album(artist));
        Album album2 = entityManager.persistAndFlush(MusicFactoryIT.album2(artist));
        album.setImage(entityManager.persistAndFlush(MusicFactoryIT.albumImage(album)));
        album2.setImage(entityManager.persistAndFlush(MusicFactoryIT.albumImage(album2)));
        entityManager.clear();

        List<Album> result = repository.findAllForMainPage();
        assertAlbumList(result, album, album2);
    }

    @Test
    void findByIdWithArtistAndImage_ShouldReturnsAlbumWithArtistAndImage(){
        Artist artist = entityManager.persistAndFlush(MusicFactoryIT.artist());
        Album album = entityManager.persistAndFlush(MusicFactoryIT.album(artist));
        album.setImage(entityManager.persistAndFlush(MusicFactoryIT.albumImage(album)));
        entityManager.clear();

        Album actualAlbum = repository.findByIdWithArtistAndImage(album.getId()).orElseThrow();
        assertAlbumWithArtistAndImage(actualAlbum, album);
    }

    @Test
    void findByIdWithArtistAndImage_ShouldReturnsEmpty_WhenIdIsInvalid(){
        Artist artist = entityManager.persistAndFlush(MusicFactoryIT.artist());
        Album album = entityManager.persistAndFlush(MusicFactoryIT.album(artist));
        album.setImage(entityManager.persistAndFlush(MusicFactoryIT.albumImage(album)));
        entityManager.clear();

        assertThat(repository.findByIdWithArtistAndImage(156L)).isEmpty();
    }

    @Test
    void findAllByIdForCollectionPage_ShouldReturnsValidAlbumList(){
        Artist artist = entityManager.persistAndFlush(MusicFactoryIT.artist());
        Album album = entityManager.persistAndFlush(MusicFactoryIT.album(artist));
        Album album2 = entityManager.persistAndFlush(MusicFactoryIT.album2(artist));
        album.setImage(entityManager.persistAndFlush(MusicFactoryIT.albumImage(album)));
        album2.setImage(entityManager.persistAndFlush(MusicFactoryIT.albumImage(album2)));
        List<Long> albumIds = List.of(album.getId(), album2.getId());
        entityManager.clear();

        List<Album> result = repository.findAllByIdForCollectionPage(albumIds);
        assertAlbumList(result, album, album2);
    }

    private void assertAlbumList(List<Album> result, Album album, Album album2){
        assertThat(result)
                .extracting(Album::getId, Album::getTitle, albumEntity -> albumEntity.getArtist().getId(), albumEntity -> albumEntity.getImage().getId())
                .containsExactlyInAnyOrder(
                        tuple(album.getId(), album.getTitle(), album.getArtist().getId(), album.getImage().getId()),
                        tuple(album2.getId(), album2.getTitle(), album2.getArtist().getId(), album2.getImage().getId())
                );
    }

    private void assertAlbumWithArtistAndImage(Album actual, Album expected){
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getTitle()).isEqualTo(expected.getTitle());
        assertThat(actual.getArtist().getId()).isEqualTo(expected.getArtist().getId());
        assertThat(actual.getArtist().getName()).isEqualTo(expected.getArtist().getName());
        assertThat(actual.getImage().getId()).isEqualTo(expected.getImage().getId());
        assertThat(actual.getImage().getKey()).isEqualTo(expected.getImage().getKey());
    }
}
