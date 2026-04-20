package org.musicservice.demo.support.factory.it.music;

import org.musicservice.demo.entity.genre.Genre;
import org.musicservice.demo.entity.image.AlbumImage;
import org.musicservice.demo.entity.likes.AlbumLike;
import org.musicservice.demo.entity.likes.SoundLike;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.entity.music.Sound;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.integration.jamendo.response.MusicResponse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MusicFactoryIT {

    public static List<MusicResponse> musicResponseList(int size){
        List<MusicResponse> responseList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            responseList.add(new MusicResponse(
                    "track_" + i,
                    250,
                    "Mr.Kitty",
                    "Time",
                    LocalDate.of(2008, 10, 29),
                    "https://album_" + i,
                    "https://track_" + i + "_download",
                    true,
                    null, null));
        }
        return responseList;
    }

    public static Artist artist(Genre genre) {
        return new Artist("Muse", genre);
    }

    public static Album album(Artist artist, Genre genre) {
        return new Album("Black Holes and Revelations", LocalDate.of(2003, 3,5), artist, genre);
    }

    public static AlbumImage albumImage(Album album) {
        return new AlbumImage(UUID.randomUUID() + ".jpg", album);
    }

    public static Sound sound(Artist artist, Album album, Genre genre){
        return new Sound("Supermassive Black Hole", 280,
                artist, album, album.getTitle() + "/supermassive_black_hole.mp3",
                LocalDate.of(2003, 3,5), genre);
    }

    public static AlbumLike albumLike(User user, Album album){
        return new AlbumLike(user, album);
    }

    public static SoundLike soundLike(User user, Sound sound){
        return new SoundLike(user, sound);
    }
}

