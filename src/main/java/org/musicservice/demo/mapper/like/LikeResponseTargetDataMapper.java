package org.musicservice.demo.mapper.like;

import org.mapstruct.Named;
import org.musicservice.demo.model.music.Album;
import org.musicservice.demo.model.music.Sound;
import org.springframework.stereotype.Component;

@Component
public class LikeResponseTargetDataMapper {

    @Named("mapTargetType")
    public String mapTargetType(Object target){
        String targetType = null;
        if(target instanceof Album){
            targetType = "album";
        } else if(target instanceof Sound){
            targetType = "sound";
        }
        return targetType;
    }

    @Named("mapTargetId")
    public Long mapTargetId(Object target){
        Long targetId = null;
        if(target instanceof Album){
            targetId = ((Album) target).getId();
        } else if(target instanceof Sound){
            targetId = ((Sound) target).getId();
        }
        return targetId;
    }
}
