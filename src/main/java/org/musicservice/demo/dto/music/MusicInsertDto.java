package org.musicservice.demo.dto.music;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Getter;
import lombok.Setter;
import org.musicservice.demo.dto.image.AlbumImageDto;
import org.musicservice.demo.dto.image.SoundImageDto;
import org.musicservice.demo.model.music.Album;
import org.musicservice.demo.model.music.Artist;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class MusicInsertDto {

    private String artist;
    private String album;
    private String title;
    private Integer duration;
    private String s3_key;
    private AlbumImageDto albumImage;
    private SoundImageDto soundImage;
}
