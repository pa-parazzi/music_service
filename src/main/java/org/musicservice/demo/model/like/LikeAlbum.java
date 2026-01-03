package org.musicservice.demo.model.like;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.musicservice.demo.model.music.Album;
import org.musicservice.demo.model.user.User;

import java.time.Instant;

@Entity
@Table(name = "like_album")
@Getter
@Setter
public class LikeAlbum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id", referencedColumnName = "id")
    private Album album;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    public LikeAlbum(){}

    public LikeAlbum(User user, Album album) {
        this.user = user;
        this.album = album;
    }
}
