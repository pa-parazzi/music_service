package org.musicservice.demo.dto.music.album;

import lombok.Data;
import org.musicservice.demo.dto.image.AlbumImageDto;
import org.musicservice.demo.dto.music.artist.ArtistResponse;

@Data
public class AlbumResponse {

    private Long albumId;
    private AlbumImageDto albumImage;
    private ArtistResponse artist;
    private String title;
}
