package org.musicservice.demo.support.fixture;

import org.musicservice.demo.entity.genre.Genre;
import org.musicservice.demo.entity.image.AlbumImage;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.repository.image.AlbumImageRepository;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.musicservice.demo.repository.music.ArtistRepository;
import org.musicservice.demo.repository.music.GenreRepository;
import org.musicservice.demo.support.factory.it.music.MusicFactoryIT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.musicservice.demo.support.assertions.PageAssertions.totalElements;

@TestComponent
public class AlbumTestFixture {

    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private AlbumRepository albumRepository;
    @Autowired
    private AlbumImageRepository albumImageRepository;

    public AlbumAggregate albumAggregateWithOneAlbum(){
        Genre genre = genreRepository.save(MusicFactoryIT.genre());
        Artist artist = artistRepository.save(MusicFactoryIT.artist(genre));
        Album album = albumRepository.save(MusicFactoryIT.album(artist, genre));
        AlbumImage albumImage = albumImageRepository.save(MusicFactoryIT.albumImage(album));
        album.setImage(albumImage);
        return new AlbumAggregate(genre, artist, List.of(album));
    }

    public AlbumAggregate albumAggregateWithAlbums(String titlePrefix, String imgKeyNameEndsWith){
        List<Album> albums = new ArrayList<>();
        Genre genre = genreRepository.save(MusicFactoryIT.genre());
        Artist artist = artistRepository.save(MusicFactoryIT.artist(genre));
        for (int i = 0; i < totalElements; i++) {
            Album album = albumRepository.save
                    (new Album(titlePrefix + "_" + i,
                            LocalDate.of(2004, 10, 15),
                            artist, genre));
            album.setImage(albumImageRepository.save
                    (new AlbumImage(i + "_" + imgKeyNameEndsWith, album)));
            albums.add(album);
        }
        return new AlbumAggregate(genre, artist, albums);
    }
}
