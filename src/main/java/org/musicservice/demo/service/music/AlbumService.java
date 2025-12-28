package org.musicservice.demo.service.music;

import org.aspectj.weaver.ast.Literal;
import org.musicservice.demo.dto.music.response.AlbumResponse;
import org.musicservice.demo.dto.music.response.CollectionAlbumsResponse;
import org.musicservice.demo.dto.music.response.LikeResponse;
import org.musicservice.demo.exception.music.AlbumNotFoundException;
import org.musicservice.demo.mapper.music.AlbumResponseMapper;
import org.musicservice.demo.model.music.Album;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final AlbumResponseMapper albumResponseMapper;

    @Autowired
    public AlbumService(AlbumRepository albumRepository, AlbumResponseMapper albumResponseMapper) {
        this.albumRepository = albumRepository;
        this.albumResponseMapper = albumResponseMapper;
    }

    public CollectionAlbumsResponse getAlbumCollectionByUserLikes(List<LikeResponse> responses){
        List<Long> albumsIds = responses.stream().map(LikeResponse::getTargetId).toList();
        Map<Long, Integer> orderAlbumsIds = new HashMap<>();
        for (int i = 0; i < albumsIds.size(); i++) {
            orderAlbumsIds.put(albumsIds.get(i), i);
        }
        List<AlbumResponse> albumResponses = albumRepository.findAllById(albumsIds).stream().map(albumResponseMapper::toAlbumResponse)
                .sorted(Comparator.comparingInt(response -> orderAlbumsIds.get(response.getAlbumId()))).toList();

        CollectionAlbumsResponse collectionAlbumsResponse = new CollectionAlbumsResponse();
        collectionAlbumsResponse.setAlbums(albumResponses);
        return collectionAlbumsResponse;
    }

    public List<AlbumResponse> findAlbumResponseStartingWith(String fragment){
        List<Album> albumList = findAllStartingWith(fragment);
        return albumList.stream().map(album -> getAlbumById(album.getId())).toList();
    }

    public List<Album> findAllStartingWith(String fragment){
        if(fragment == null || fragment.trim().isBlank()) return null;
        return albumRepository.findByTitleStartingWith(fragment);
    }

    public List<AlbumResponse> getAllAlbumResponse(){
        List<Album> albums = albumRepository.findAll();
        return albums.stream().map(albumResponseMapper::toAlbumResponse).toList();
    }

    public Album searchById(Long albumId){
        return albumRepository.searchById(albumId).orElseThrow(()->new AlbumNotFoundException("Альбом не существует"));
    }

    public AlbumResponse getAlbumById(Long albumId){
        Album album = searchById(albumId);
        return albumResponseMapper.toAlbumResponse(album);
    }
}
