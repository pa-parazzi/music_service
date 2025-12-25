package org.musicservice.demo.model.music;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.AnyDiscriminator;
import org.hibernate.annotations.AnyDiscriminatorValue;
import org.hibernate.annotations.AnyKeyJavaClass;
import org.hibernate.annotations.Any;
import org.musicservice.demo.model.user.User;

@Entity
@Table(name = "Likes")
@Getter
@Setter
public class Like {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Any
    @AnyDiscriminator(DiscriminatorType.STRING)
    @Column(name = "target_type")
    @AnyKeyJavaClass(Long.class)
    @AnyDiscriminatorValue(discriminator = "sound", entity = Sound.class)
    @AnyDiscriminatorValue(discriminator = "album", entity = Album.class)
    @JoinColumn(name = "target_id")
    private Object target;

    public Like(){}

    public Like(User user, Object target) {
        this.user = user;
        this.target = target;
    }

}
