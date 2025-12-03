package org.musicservice.demo.model.music;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "sound")
@Getter
@Setter
public class Sound {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "duration")
    private int duration;

    @Column(name = "s3_key")
    private String key;

    @ManyToOne
    @JoinColumn(name = "artist_id", referencedColumnName = "id")
    private Artist artist;

    @ManyToOne
    @JoinColumn(name = "album_id", referencedColumnName = "id")
    private Album album;

    public Sound(){}

    public Sound(String title, int duration, Artist artist, Album album, String key) {
        this.title = title;
        this.duration = duration;
        this.artist = artist;
        this.album = album;
        this.key = key;
    }

    public Sound(String title, int duration, String key){
        this.title = title;
        this.duration = duration;
        this.key = key;
    }

    public Sound(String title, int duration){
        this.title = title;
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Sound sound = (Sound) o;
        return Objects.equals(key, sound.key);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(key);
    }
}
