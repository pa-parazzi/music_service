package org.musicservice.demo.dto.music;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MusicDto {

    private ArtistDto artist;

    private List<AlbumDto> albums;

    private List<SoundDto> soundList;
}

