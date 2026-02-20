package org.musicservice.demo.dto.jamendo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class MusicResponse {

    private String name;
    private Integer duration;
    private String artist_name;
    private String album_name;
    private String album_image;
    private String audio;
    private String audiodownload;

    private String mp3Key;
    private String imgKey;
}
