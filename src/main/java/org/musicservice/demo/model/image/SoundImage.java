package org.musicservice.demo.model.image;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.musicservice.demo.model.music.Sound;

@Entity
@Table(name = "sound_image")
@Getter
@Setter
public class SoundImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "s3_key")
    private String s3Key;

    @OneToOne
    @JoinColumn(name = "sound_id", referencedColumnName = "id")
    private Sound sound;

    public SoundImage(){}

    public SoundImage(String s3Key, Sound sound) {
        this.s3Key = s3Key;
        this.sound = sound;
    }
}
