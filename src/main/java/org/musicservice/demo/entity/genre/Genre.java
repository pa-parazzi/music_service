package org.musicservice.demo.entity.genre;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "genre")
@Getter
@Setter
public class Genre {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    @Enumerated(value = EnumType.STRING)
    private GenreName name;

    @Column(name = "image_name")
    private String imageName;

    public Genre() {}

    public Genre(GenreName name, String imageName) {
        this.name = name;
        this.imageName = imageName;
    }
}
