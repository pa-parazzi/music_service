package org.musicservice.demo.dto.music.search;

import lombok.Data;
import org.musicservice.demo.dto.music.album.AlbumResponse;
import org.musicservice.demo.dto.music.artist.ArtistResponse;

import java.util.List;

@Data
public class SearchArtistAndAlbumResponse {

    private List<ArtistResponse> artists;
    private List<AlbumResponse> albums;
}
