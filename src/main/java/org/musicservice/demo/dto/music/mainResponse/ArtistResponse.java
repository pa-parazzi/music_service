package org.musicservice.demo.dto.music.mainResponse;

import lombok.Data;
import org.musicservice.demo.dto.music.SoundDto;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

@Data
public class ArtistResponse {

    private String name;
    private List<SoundDto> soundList;
}
