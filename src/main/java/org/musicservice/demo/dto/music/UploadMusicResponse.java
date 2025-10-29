package org.musicservice.demo.dto.music;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.musicservice.demo.dto.image.AlbumImageDto;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class UploadMusicResponse {

    private String name;
    private Integer duration;
    private String artist_name;
    private String album_name;
    private String audio;
    private String album_image;
}
