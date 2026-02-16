package org.musicservice.demo.support.factory.it.music;

import org.musicservice.demo.entity.image.AlbumImage;
import org.musicservice.demo.entity.like.LikeAlbum;
import org.musicservice.demo.entity.like.LikeSound;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.entity.music.Sound;
import org.musicservice.demo.entity.user.User;

import java.util.List;

public class MusicFactoryIT {

    public static Artist artist() {
        return new Artist("Muse");
    }

    public static List<Album> albumList(Artist artist) {
        return List.of(album(artist), album2(artist), album3(artist));
    }

    public static Album album(Artist artist) {
        return new Album("Black Holes and Revelations", artist);
    }

    public static Album album2(Artist artist) {
        return new Album("The Resistance", artist);
    }

    public static Album album3(Artist artist) {
        return new Album("Absolution", artist);
    }

    public static AlbumImage albumImage(Album album) {
        return new AlbumImage("some_image.jpg", album);
    }

    public static List<Sound> soundList(Artist artist, Album album) {
        Sound sound1 = new Sound("Supermassive Black Hole", 280,
                artist, album, album.getTitle() + "/supermassive_black_hole.mp3");
        Sound sound2 = new Sound("Take a Bow", 380,
                artist, album, album.getTitle() + "/take_a_bow.mp3");
        Sound sound3 = new Sound("Starlight", 345,
                artist, album, album.getTitle() + "/starlight.mp3");
        return List.of(sound1, sound2, sound3);
    }

    public static LikeAlbum likeAlbum (User user, Album album){
        return new LikeAlbum(user, album);
    }

    public static LikeSound likeSound (User user, Sound sound){
        return new LikeSound(user, sound);
    }
}

