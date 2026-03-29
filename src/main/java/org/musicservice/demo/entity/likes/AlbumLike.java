package org.musicservice.demo.entity.likes;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.user.User;

import java.time.Instant;

@Entity
@Table(name = "album_like")
@Getter
@Setter
public class AlbumLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    private Album album;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    public AlbumLike(){}

    public AlbumLike(User user, Album album) {
        this.user = user;
        this.album = album;
    }
}
