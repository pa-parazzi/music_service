package org.musicservice.demo.model.image;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.musicservice.demo.model.music.Album;
import org.yaml.snakeyaml.events.Event;

@Entity
@Table(name = "album_image")
@Getter
@Setter
public class AlbumImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "s3_key")
    private String key;

    @OneToOne
    @JoinColumn(name = "album_id", referencedColumnName = "id")
    private Album album;

    public AlbumImage(){}

    public AlbumImage(String key, Album album) {
        this.key = key;
        this.album = album;
    }

    public AlbumImage(String key) {
        this.key = key;
    }

}
