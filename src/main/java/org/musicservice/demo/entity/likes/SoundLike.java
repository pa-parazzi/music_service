package org.musicservice.demo.entity.likes;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.musicservice.demo.entity.music.Sound;
import org.musicservice.demo.entity.user.User;

import java.time.Instant;


@Entity
@Table(name = "sound_like")
@Getter
@Setter
public class SoundLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sound_id", referencedColumnName = "id")
    private Sound sound;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public SoundLike(){}

    public SoundLike(User user, Sound sound) {
        this.user = user;
        this.sound = sound;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        SoundLike that = (SoundLike) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
