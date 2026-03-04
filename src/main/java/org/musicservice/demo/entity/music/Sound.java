package org.musicservice.demo.entity.music;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    public Sound(){}

    public Sound(String title, int duration, Artist artist, Album album, String key) {
        this.title = title;
        this.duration = duration;
        this.artist = artist;
        this.album = album;
        this.key = key;
    }

}
