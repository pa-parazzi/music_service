package org.musicservice.demo.support.factory.unit.music;

import org.musicservice.demo.dto.image.ImageResponse;
import org.musicservice.demo.dto.music.album.AlbumResponse;
import org.musicservice.demo.dto.music.artist.ArtistResponse;
import org.musicservice.demo.dto.music.sound.SoundResponse;
import org.musicservice.demo.integration.jamendo.response.MusicResponse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MusicDataFactory {

    public static ArtistResponse artistResponse(){
        return new ArtistResponse(1L, "Mr.Kitty");
    }

    public static ImageResponse albumImageResponse(){
        return new ImageResponse("time_key", "http:/cloud_time.com");
    }

    public static AlbumResponse albumResponse(){
        return new AlbumResponse(1L, "Time", LocalDate.of(2020, 1, 1),albumImageResponse(), artistResponse());
    }

    public static SoundResponse soundResponse(){
        return new SoundResponse(1L, "After Dark", 270,
                "after_dark.mp3","http:/cloud_after_dark.com");
    }

    public static List<MusicResponse> musicResponseList(int size){
        List<MusicResponse> musicResponseList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            musicResponseList.add(musicResponse());
        }
        return musicResponseList;
    }

    public static MusicResponse musicResponse(){
        return new MusicResponse(
                "bad romance",
                285,
                "Lady Gaga",
                "Gaga",
                LocalDate.of(2002,3,11),
                "album-image",
                "audio-download",
                true,
                null, null);
    }

    public static MusicResponse musicResponseWithAudioDownloadAllowedIsFalse(){
        return new MusicResponse(
                "bad romance",
                285,
                "Lady Gaga",
                "Gaga",
                LocalDate.of(2002,3,11),
                "album-image",
                "audio-download",
                false,
                null, null);
    }

    public static MusicResponse musicResponseWithDurationIsZero(){
        return new MusicResponse(
                "bad romance",
                0,
                "Lady Gaga",
                "Gaga",
                LocalDate.of(2002,3,11),
                "album-image",
                "audio-download",
                true,
                null, null);
    }
}
