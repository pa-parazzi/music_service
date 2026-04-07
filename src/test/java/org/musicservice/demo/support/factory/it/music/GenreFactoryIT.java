package org.musicservice.demo.support.factory.it.music;

import org.musicservice.demo.entity.genre.Genre;
import org.musicservice.demo.entity.genre.GenreName;

import java.util.List;

public class GenreFactoryIT {

    public static List<Genre> genres(){
        return List.of(
                new Genre(GenreName.ROCK, "rock.jpg"),
                new Genre(GenreName.POP, "pop.jpg"),
                new Genre(GenreName.JAZZ, "jazz.jpg"),
                new Genre(GenreName.BLUES, "blues.jpg"),
                new Genre(GenreName.DANCE, "dance.jpg"));
    }
}
