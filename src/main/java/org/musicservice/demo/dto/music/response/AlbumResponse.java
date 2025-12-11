package org.musicservice.demo.dto.music.response;

import lombok.Data;
import org.musicservice.demo.dto.image.AlbumImageDto;
import org.musicservice.demo.dto.music.ArtistDto;
import org.musicservice.demo.dto.music.SoundDto;

import java.util.List;

@Data
public class AlbumResponse {

    private Long albumId;
    private AlbumImageDto albumImage;
    private ArtistDto artist;
    private String title;
    private List<SoundDto> soundList;
}
