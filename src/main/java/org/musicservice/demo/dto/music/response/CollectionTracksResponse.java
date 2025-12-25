package org.musicservice.demo.dto.music.response;

import lombok.Data;
import org.musicservice.demo.dto.music.SoundDto;

import java.util.List;

@Data
public class CollectionTracksResponse {

    private List<SoundDto> soundList;
}
