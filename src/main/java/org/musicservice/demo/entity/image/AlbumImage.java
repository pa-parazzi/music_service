package org.musicservice.demo.entity.image;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.musicservice.demo.entity.music.Album;

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id", referencedColumnName = "id")
    private Album album;

    public AlbumImage(){}

    public AlbumImage(String key, Album album) {
        this.key = key;
        this.album = album;
    }

}
