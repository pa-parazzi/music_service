package org.musicservice.demo.entity.music;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.musicservice.demo.entity.genre.Genre;
import org.musicservice.demo.entity.image.AlbumImage;

@Entity
@Table(name = "album")
@Getter
@Setter
public class Album{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id")
    private Artist artist;

    @OneToOne(mappedBy = "album")
    private AlbumImage image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id")
    private Genre genre;

    public Album(){}

    public Album(String title, Artist artist, Genre genre) {
        this.title = title;
        this.artist = artist;
        this.genre = genre;
    }
}
