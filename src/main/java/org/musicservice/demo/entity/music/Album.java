package org.musicservice.demo.entity.music;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
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

    @OneToOne(mappedBy = "album", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private AlbumImage image;

    public Album(){}

    public Album(String title, Artist artist) {
        this.title = title;
        this.artist = artist;
    }

    public void setImage(AlbumImage albumImage){
        this.image = albumImage;
        albumImage.setAlbum(this);
    }
}
