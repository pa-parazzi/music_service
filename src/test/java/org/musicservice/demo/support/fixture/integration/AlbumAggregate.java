package org.musicservice.demo.support.fixture.integration;

import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.music.Artist;

import java.util.List;

public record AlbumAggregate(Artist artist, List<Album> albums) {}