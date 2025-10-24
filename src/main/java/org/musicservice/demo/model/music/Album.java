package org.musicservice.demo.model.music;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.musicservice.demo.model.image.AlbumImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "album")
@Getter
@Setter
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @ManyToOne
    @JoinColumn(name = "artist_id", referencedColumnName = "id")
    private Artist artist ;

    @OneToMany(mappedBy = "album")
    private List<Sound> soundList = new ArrayList<>();

    @OneToOne(mappedBy = "album", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private AlbumImage image;

    public Album(){}

    public Album(String title, Artist artist, List<Sound> soundList, AlbumImage image) {
        this.title = title;
        this.artist = artist;
        this.soundList = soundList;
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Album album = (Album) o;
        return Objects.equals(title, album.title);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(title);
    }
}
