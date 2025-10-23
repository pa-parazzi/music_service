package org.musicservice.demo.model.music;

import jakarta.persistence.*;
import org.hibernate.annotations.Fetch;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "album")
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

    public Album(){}

    public Album(String title, Artist artist, List<Sound> soundList) {
        this.title = title;
        this.artist = artist;
        this.soundList = soundList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public List<Sound> getSoundList() {
        return soundList;
    }

    public void setSoundList(List<Sound> soundList) {
        this.soundList = soundList;
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
