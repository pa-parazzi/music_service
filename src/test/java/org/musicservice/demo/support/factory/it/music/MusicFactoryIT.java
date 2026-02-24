package org.musicservice.demo.support.factory.it.music;

import org.musicservice.demo.entity.image.AlbumImage;
import org.musicservice.demo.entity.like.LikeAlbum;
import org.musicservice.demo.entity.like.LikeSound;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.entity.music.Sound;
import org.musicservice.demo.entity.user.User;

import java.util.List;
import java.util.UUID;

public class MusicFactoryIT {

    public static Artist artist() {
        return new Artist("Muse");
    }

    public static Artist artist2() {
        return new Artist("Tri Face");
    }

    public static List<Album> albumList(Artist artist) {
        return List.of(album(artist), album2(artist), album3(artist));
    }

    public static Album album(Artist artist) {
        return new Album("Black Holes and Revelations", artist);
    }

    public static Album album2(Artist artist) {
        return new Album("Mercy", artist);
    }

    public static Album album3(Artist artist) {
        return new Album("Absolution", artist);
    }

    public static AlbumImage albumImage(Album album) {
        return new AlbumImage(UUID.randomUUID().toString() + ".jpg", album);
    }

    public static Sound sound(Artist artist, Album album){
        return new Sound("Supermassive Black Hole", 280,
                artist, album, album.getTitle() + "/supermassive_black_hole.mp3");
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

    public static List<Sound> soundListByAlbum(Artist artist, Album album) {
        Sound sound1 = new Sound("Supermassive Black Hole", 280,
                artist, album, album.getTitle() + "/supermassive_black_hole.mp3");
        Sound sound2 = new Sound("Take a Bow", 380,
                artist, album, album.getTitle() + "/take_a_bow.mp3");
        Sound sound3 = new Sound("Starlight", 345,
                artist, album, album.getTitle() + "/starlight.mp3");
        Sound sound4 = new Sound("Map of the Problematique", 358,
                artist, album, album.getTitle() +"/map_of_the_problematique.mp3");
        Sound sound5 = new Sound("Soldier's Poem", 198,
                artist, album, album.getTitle() +"/soldier's_poem.mp3");
        return List.of(sound1, sound2, sound3, sound4, sound5);
    }

    public static List<Sound> soundListByArtist(Artist artist, Album album) {
        Sound sound1 = new Sound("Madness", 190,
                artist, album, album.getTitle() + "/madness.mp3");
        Sound sound2 = new Sound("Plug in Baby", 263,
                artist, album, album.getTitle() + "/plug_in_baby.mp3");
        Sound sound3 = new Sound("Starlight", 345,
                artist, album, album.getTitle() + "/starlight.mp3");
        Sound sound4 = new Sound("Psycho", 242,
                artist, album, album.getTitle() + "/psycho.mp3");
        Sound sound5 = new Sound("Undisclosed Desires", 295,
                artist, album, album.getTitle() + "/undisclosed_desires.mp3");
        return List.of(sound1, sound2, sound3, sound4, sound5);
    }

    public static LikeAlbum likeAlbum (User user, Album album){
        return new LikeAlbum(user, album);
    }

    public static LikeSound likeSound (User user, Sound sound){
        return new LikeSound(user, sound);
    }
}

