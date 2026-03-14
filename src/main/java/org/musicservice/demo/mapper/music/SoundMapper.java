package org.musicservice.demo.mapper.music;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.musicservice.demo.dto.music.sound.SoundPageProjection;
import org.musicservice.demo.dto.music.sound.SoundPageResponse;
import org.musicservice.demo.dto.music.sound.SoundResponse;
import org.musicservice.demo.entity.music.Sound;
import org.musicservice.demo.mapper.image.ImageUrlMapper;

@Mapper(componentModel = "spring", uses = {SoundUrlMapper.class, ImageUrlMapper.class})
public interface SoundMapper {

    @Mapping(target = "url", source = "key", qualifiedByName = "mapMp3Url")
    SoundResponse toResponse(Sound sound);

    @Mapping(target = "url", source = "key", qualifiedByName = "mapMp3Url")
    @Mapping(target = "album.albumImageUrl", source = "album.albumImageKey", qualifiedByName = "mapImgUrl")
    SoundPageResponse toPageResponse(SoundPageProjection soundProjection);
}
