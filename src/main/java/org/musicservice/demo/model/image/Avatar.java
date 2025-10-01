package org.musicservice.demo.model.image;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.musicservice.demo.model.user.User;

@Entity
@Table(name="images_avatar")
@Getter
@Setter
public class Avatar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "s3_key")
    private String key;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User owner;

    public Avatar(){}

    public Avatar(String key, User owner) {
        this.key = key;
        this.owner = owner;
    }


}
