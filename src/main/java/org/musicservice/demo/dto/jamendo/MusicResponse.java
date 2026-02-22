package org.musicservice.demo.dto.jamendo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MusicResponse {

    private String name;
    private Integer duration;
    private String artist_name;
    private String album_name;

    private String mp3Key;
    private String albumImgKey;
}
