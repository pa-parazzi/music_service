package org.musicservice.demo.model.music;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "artist")
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "artist", orphanRemoval = true)
    private List<Album> albums = new ArrayList<>();

    @OneToMany(mappedBy = "artist", orphanRemoval = true)
    private List<Sound> soundList = new ArrayList<>();

    public Artist(){}

    public Artist(String name, List<Album> albums, List<Sound> soundList) {
        this.name = name;
        this.albums = albums;
        this.soundList = soundList;
    }

    public Artist(String name){
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Artist artist = (Artist) o;
        return id.equals(artist.id) && name.equals(artist.name) && albums.equals(artist.albums) && soundList.equals(artist.soundList);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + albums.hashCode();
        result = 31 * result + soundList.hashCode();
        return result;
    }
}
