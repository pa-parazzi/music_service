package org.musicservice.demo.support.factory.it.music;

import org.musicservice.demo.entity.genre.Genre;
import org.musicservice.demo.entity.genre.GenreName;
import org.musicservice.demo.entity.image.AlbumImage;
import org.musicservice.demo.entity.likes.AlbumLike;
import org.musicservice.demo.entity.likes.SoundLike;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.entity.music.Sound;
import org.musicservice.demo.entity.user.User;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class MusicFactoryIT {

    public static Genre genre(){
        return new Genre(GenreName.ROCK, "rock.jpg");
    }

    public static Artist artist(Genre genre) {
        return new Artist("Muse", genre);
    }

    public static Artist artist2(Genre genre) {
        return new Artist("Tri Face", genre);
    }

    public static Artist artist3(Genre genre) {
        return new Artist("Tears for Fears", genre);
    }

    public static List<Album> albumList(Artist artist, Genre genre) {
        return List.of(album(artist, genre), album2(artist, genre), album3(artist, genre));
    }

    public static Album album(Artist artist, Genre genre) {
        return new Album("Black Holes and Revelations", LocalDate.of(2003, 3,5), artist, genre);
    }

    public static Album album2(Artist artist, Genre genre) {
        return new Album("Mercy", LocalDate.of(2003, 3,5), artist, genre);
    }

    public static Album album3(Artist artist, Genre genre) {
        return new Album("Absolution", LocalDate.of(2003, 3,5), artist, genre);
    }

    public static Album album4(Artist artist, Genre genre) {
        return new Album("Acrobat", LocalDate.of(2003, 3,5), artist, genre);
    }

    public static AlbumImage albumImage(Album album) {
        return new AlbumImage(UUID.randomUUID() + ".jpg", album);
    }

    public static Sound sound(Artist artist, Album album, Genre genre){
        return new Sound("Supermassive Black Hole", 280,
                artist, album, album.getTitle() + "/supermassive_black_hole.mp3",
                LocalDate.of(2003, 3,5), genre);
    }

    public static Sound sound2(Artist artist, Album album, Genre genre){
        return new Sound("Take a Bow", 380,
                artist, album, album.getTitle() + "/take_a_bow.mp3",
                LocalDate.of(2008, 2,5), genre);
    }

    public static Sound sound3(Artist artist, Album album, Genre genre){
        return new Sound("Starlight", 345,
                artist, album, album.getTitle() + "/starlight.mp3",
                LocalDate.of(2003, 3,5), genre);
    }

    public static List<Sound> soundList(Artist artist, Album album, Genre genre) {
        return List.of(sound(artist, album, genre), sound2(artist, album, genre), sound3(artist, album, genre));
    }

    public static List<Sound> soundListByAlbum(Artist artist, Album album, Genre genre) {
        Sound sound1 = new Sound("Supermassive Black Hole", 280,
                artist, album, album.getTitle() + "/supermassive_black_hole.mp3",
                LocalDate.of(2003, 3,5), genre);
        Sound sound2 = new Sound("Take a Bow", 380,
                artist, album, album.getTitle() + "/take_a_bow.mp3",
                LocalDate.of(2003, 3,5), genre);
        Sound sound3 = new Sound("Starlight", 345,
                artist, album, album.getTitle() + "/starlight.mp3",
                LocalDate.of(2003, 3,5), genre);
        Sound sound4 = new Sound("Map of the Problematique", 358,
                artist, album, album.getTitle() +"/map_of_the_problematique.mp3",
                LocalDate.of(2003, 3,5), genre);
        Sound sound5 = new Sound("Soldier's Poem", 198,
                artist, album, album.getTitle() +"/soldier's_poem.mp3",
                LocalDate.of(2003, 3,5), genre);
        return List.of(sound1, sound2, sound3, sound4, sound5);
    }

    public static List<Sound> soundListByArtist(Artist artist, Album album, Genre genre) {
        Sound sound1 = new Sound("Madness", 190,
                artist, album, album.getTitle() + "/madness.mp3",
                LocalDate.of(2012, 5,23), genre);
        Sound sound2 = new Sound("Plug in Baby", 263,
                artist, album, album.getTitle() + "/plug_in_baby.mp3",
                LocalDate.of(2012, 5,23), genre);
        Sound sound3 = new Sound("Starlight", 345,
                artist, album, album.getTitle() + "/starlight.mp3",
                LocalDate.of(2012, 5,23), genre);
        Sound sound4 = new Sound("Psycho", 242,
                artist, album, album.getTitle() + "/psycho.mp3",
                LocalDate.of(2012, 5,23), genre);
        Sound sound5 = new Sound("Undisclosed Desires", 295,
                artist, album, album.getTitle() + "/undisclosed_desires.mp3",
                LocalDate.of(2012, 5,23), genre);
        return List.of(sound1, sound2, sound3, sound4, sound5);
    }

    public static AlbumLike albumLike(User user, Album album){
        return new AlbumLike(user, album);
    }

    public static SoundLike soundLike(User user, Sound sound){
        return new SoundLike(user, sound);
    }
}

