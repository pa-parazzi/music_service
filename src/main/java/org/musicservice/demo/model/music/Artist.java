package org.musicservice.demo.model.music;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "artist")
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Album> albums = new ArrayList<>();

    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sound> soundList = new ArrayList<>();

    public Artist(){}

    public Artist(String name, List<Album> albums, List<Sound> soundList) {
        this.name = name;
        this.albums = albums;
        this.soundList = soundList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    public List<Sound> getSoundList() {
        return soundList;
    }

    public void setSoundList(List<Sound> soundList) {
        this.soundList = soundList;
    }
}
