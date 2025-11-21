package org.musicservice.demo.dto.music.mainResponse;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.musicservice.demo.dto.music.ArtistDto;
import org.musicservice.demo.dto.music.SoundDto;

import java.util.List;

@Data
public class MainResponse {
    private List<AlbumResponse> albums;
}
