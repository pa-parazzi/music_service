package org.musicservice.demo.dto.music.response;

import lombok.Data;
import org.musicservice.demo.dto.music.ArtistDto;

import java.util.List;

@Data
public class SearchArtistAndAlbumResponse {

    private List<ArtistDto> artists;
    private List<AlbumResponse> albums;
}
