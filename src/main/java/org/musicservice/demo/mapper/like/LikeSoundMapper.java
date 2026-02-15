package org.musicservice.demo.mapper.like;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.musicservice.demo.dto.like.LikedSoundId;
import org.musicservice.demo.entity.like.LikeSound;

@Mapper(componentModel = "spring")
public interface LikeSoundMapper {

    @Mapping(target = "soundId", source = "sound.id")
    LikedSoundId toResponse(LikeSound likeSound);
}
