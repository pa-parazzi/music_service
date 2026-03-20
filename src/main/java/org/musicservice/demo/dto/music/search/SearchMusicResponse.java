package org.musicservice.demo.dto.music.search;

import org.musicservice.demo.dto.music.album.AlbumResponse;
import org.musicservice.demo.dto.music.artist.ArtistResponse;
import org.musicservice.demo.dto.music.sound.SoundResponse;

import java.util.List;

public record SearchMusicResponse(List<SoundResponse> tracks, List<AlbumResponse> albums, List<ArtistResponse> artists) {}