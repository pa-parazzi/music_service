package org.musicservice.demo.model.music;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.musicservice.demo.model.image.AlbumImage;
import org.musicservice.demo.model.like.LikeAlbum;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    @JoinColumn(name = "artist_id", referencedColumnName = "id")
    private Artist artist;

    @OneToMany(mappedBy = "album")
    private List<Sound> soundList = new ArrayList<>();

    @OneToOne(mappedBy = "album", orphanRemoval = true)
    private AlbumImage image;

    public Album(){}

    public Album(String title, Artist artist, List<Sound> soundList, AlbumImage image) {
        this.title = title;
        this.artist = artist;
        this.soundList = soundList;
        this.image = image;
    }

    public Album(String title, AlbumImage image){
        this.title = title;
        this.image = image;
    }

    public Album(String title){
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Album other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
