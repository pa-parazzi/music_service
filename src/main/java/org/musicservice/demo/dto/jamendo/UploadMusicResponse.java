package org.musicservice.demo.dto.jamendo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class UploadMusicResponse {

    private String name;
    private Integer duration;
    private String artist_name;
    private String album_name;
    private String audio;
    private String audiodownload;
    private String album_image;

    private String mp3Key;
    private String imgKey;
}
