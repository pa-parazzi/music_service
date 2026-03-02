package org.musicservice.demo.service.music;

import org.musicservice.demo.dto.likes.LikedContentIds;
import org.musicservice.demo.dto.music.album.AlbumListResponse;
import org.musicservice.demo.dto.music.album.AlbumResponse;
import org.musicservice.demo.exception.music.MusicNotFoundException;
import org.musicservice.demo.exception.music.NoSuchMusicResultException;
import org.musicservice.demo.mapper.music.AlbumMapper;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;

    @Autowired
    public AlbumService(AlbumRepository albumRepository, AlbumMapper albumMapper) {
        this.albumRepository = albumRepository;
        this.albumMapper = albumMapper;
    }

    public AlbumListResponse getAlbumCollectionByUserLikes(LikedContentIds likedContentIds){
        if(likedContentIds.ids().isEmpty()) throw new NoSuchMusicResultException("У вас нет понравившихся альбомов");
        List<Long> orderIds = likedContentIds.ids(); // порядок элементов сохранен
        List<AlbumResponse> unorderResponse = albumRepository.findAllByIdForCollectionPage(orderIds).stream().map(albumMapper::toAlbumResponse).toList(); // порядок элементов не сохранился
        Map<Long, AlbumResponse> mapById = unorderResponse.stream().collect(Collectors.toMap(AlbumResponse::getAlbumId, Function.identity()));
        List<AlbumResponse> orderedResponse = orderIds.stream().map(mapById::get).toList();
        return new AlbumListResponse(orderedResponse);
    }

    public AlbumListResponse getAllAlbumsByMainResponse(){
        List<AlbumResponse> albumResponseList = albumRepository.findAllForMainPage().stream().map(albumMapper::toAlbumResponse).toList();
        return new AlbumListResponse(albumResponseList);
    }

    public AlbumResponse findByIdWithArtistAndImage(Long id){
        return albumRepository.findByIdWithArtistAndImage(id).map(albumMapper::toAlbumResponse).orElseThrow(()->new MusicNotFoundException("Album with id: " + id + " not found"));
    }
}
