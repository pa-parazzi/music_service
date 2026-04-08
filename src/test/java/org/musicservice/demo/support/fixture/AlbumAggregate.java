package org.musicservice.demo.support.fixture;

import org.musicservice.demo.entity.genre.Genre;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.music.Artist;

import java.util.List;

public record AlbumAggregate(Genre genre, Artist artist, List<Album> albums) {}