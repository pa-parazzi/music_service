package org.musicservice.demo.integration.jamendo.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class MusicResponse {

    private String name;
    private Integer duration;
    private String artist_name;
    private String album_name;
    private LocalDate releasedate;
    private String album_image;
    private String audiodownload;
    private Boolean audiodownload_allowed;

    private String mp3Key;
    private String albumImgKey;
}
