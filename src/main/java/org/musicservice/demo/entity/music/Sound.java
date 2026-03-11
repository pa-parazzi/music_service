package org.musicservice.demo.entity.music;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "sound")
@Getter
@Setter
public class Sound{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "s3_key")
    private String key;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id")
    private Artist artist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    private Album album;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id")
    private Genre genre;

    public Sound(){}

    public Sound(String title, Integer duration, Artist artist, Album album, String key, LocalDate releaseDate, Genre genre) {
        this.title = title;
        this.duration = duration;
        this.artist = artist;
        this.album = album;
        this.key = key;
        this.releaseDate = releaseDate;
        this.genre = genre;
    }

}
