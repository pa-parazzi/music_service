package org.musicservice.demo.entity.like;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.musicservice.demo.entity.music.Sound;
import org.musicservice.demo.entity.user.User;

import java.time.Instant;


@Entity
@Table(name = "like_sound")
@Getter
@Setter
public class LikeSound {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sound_id", referencedColumnName = "id")
    private Sound sound;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    public LikeSound(){}

    public LikeSound(User user, Sound sound) {
        this.user = user;
        this.sound = sound;
    }
}
