package org.musicservice.demo.dto.music.sound;

import lombok.Data;

import java.util.List;

@Data
public class CollectionTracksResponse {

    private List<SoundDto> soundList;
}
