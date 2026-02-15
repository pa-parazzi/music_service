package org.musicservice.demo.mapper.like;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.musicservice.demo.dto.like.LikedAlbumId;
import org.musicservice.demo.entity.like.LikeAlbum;

@Mapper(componentModel = "spring")
public interface LikeAlbumMapper {

    @Mapping(target = "albumId", source = "album.id")
    LikedAlbumId toResponse(LikeAlbum likeAlbum);

}
