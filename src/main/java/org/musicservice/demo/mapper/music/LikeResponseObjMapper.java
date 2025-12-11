package org.musicservice.demo.mapper.music;

import org.mapstruct.Named;
import org.musicservice.demo.dto.music.response.LikeResponse;
import org.musicservice.demo.model.music.Album;
import org.musicservice.demo.model.music.Sound;
import org.musicservice.demo.model.user.User;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.musicservice.demo.repository.music.LikeRepository;
import org.musicservice.demo.repository.music.SoundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LikeResponseObjMapper {

    @Named("mapUserId")
    public Long mapUserId(User user){
        return user.getId();
    }

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
