package org.musicservice.demo.support.factory.unit.music;

import org.musicservice.demo.dto.image.ImageResponse;
import org.musicservice.demo.dto.metadata.TrackMetadata;
import org.musicservice.demo.dto.music.album.AlbumResponse;
import org.musicservice.demo.dto.music.artist.ArtistResponse;
import org.musicservice.demo.dto.music.sound.SoundResponse;
import org.musicservice.demo.integration.jamendo.response.MusicResponse;

import java.time.LocalDate;

public class MusicDataFactory {

    public static ArtistResponse artistResponse(){
        return new ArtistResponse(1L, "Mr.Kitty");
    }

    public static ImageResponse albumImageResponse(){
        return new ImageResponse("time_key", "http:/cloud_time.com");
    }

    public static AlbumResponse albumResponse(){
        return new AlbumResponse(1L, "Time", albumImageResponse(), artistResponse());
    }

    public static SoundResponse soundResponse(){
        return new SoundResponse(1L, "After Dark", 270,
                "after_dark.mp3","http:/cloud_after_dark.com");
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

    public static TrackMetadata trackMetadata(){
        return new TrackMetadata(
                "bad romance",
                "Gaga",
                "gaga.jpg",
                "https://bad_romance.mp3",
                "bad_romance_key",
                "gaga-image_key");
    }
}
