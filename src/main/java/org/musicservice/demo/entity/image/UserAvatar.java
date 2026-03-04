package org.musicservice.demo.entity.image;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.musicservice.demo.entity.user.User;

@Entity
@Table(name="user_avatar")
@Getter
@Setter
public class UserAvatar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "s3_key")
    private String key;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public UserAvatar(){}

    public UserAvatar(User user, String key) {
        this.user = user;
        this.key = key;
    }

}
