package org.musicservice.demo.dto.music.mainResponse;

import lombok.Getter;
import lombok.Setter;
import org.musicservice.demo.dto.music.ArtistDto;
import org.musicservice.demo.dto.music.SoundDto;

import java.util.List;

@Getter
@Setter
public class AlbumResponse {

    private ArtistDto artist;
    private String title;
    private List<SoundDto> soundList;
}
