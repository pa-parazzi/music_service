package org.musicservice.demo.integration.jamendo.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MusicResponse(
        String name,
        Integer duration,
        String artist_name,
        String album_name,
        LocalDate releasedate,
        String album_image,
        String audiodownload,
        Boolean audiodownload_allowed,
        String mp3Key,
        String albumImgKey) {

    public MusicResponse withKeys(String mp3Key, String albumImgKey){
        return new MusicResponse(
                name,
                duration,
                artist_name,
                album_name,
                releasedate,
                album_image,
                audiodownload,
                audiodownload_allowed,
                mp3Key,
                albumImgKey);
    }
}