package org.musicservice.demo.dto.music.search;
import org.musicservice.demo.dto.music.album.AlbumResponse;
import org.musicservice.demo.dto.music.artist.ArtistResponse;

import java.util.List;

public record SearchMusicResponse(List<ArtistResponse> artists, List<AlbumResponse> albums) {}
