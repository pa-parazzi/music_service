package org.musicservice.demo.service.music;

import org.musicservice.demo.dto.music.response.AlbumResponse;
import org.musicservice.demo.exception.music.AlbumNotFoundException;
import org.musicservice.demo.mapper.music.AlbumResponseMapper;
import org.musicservice.demo.model.music.Album;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
