package org.musicservice.demo.dto.music.response;

import lombok.Data;
import org.musicservice.demo.dto.music.SoundDto;

import java.util.List;

@Data
public class ArtistResponse {

    private String name;
    private List<SoundDto> soundList;
}
