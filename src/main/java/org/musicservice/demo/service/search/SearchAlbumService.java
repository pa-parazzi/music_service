package org.musicservice.demo.service.search;

import org.musicservice.demo.dto.music.response.AlbumResponse;
import org.musicservice.demo.mapper.music.AlbumResponseMapper;
import org.musicservice.demo.model.music.Album;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SearchAlbumService {

    private final AlbumRepository albumRepository;
    private final AlbumResponseMapper albumResponseMapper;

    @Autowired
    public SearchAlbumService(AlbumRepository albumRepository, AlbumResponseMapper albumResponseMapper) {
        this.albumRepository = albumRepository;
        this.albumResponseMapper = albumResponseMapper;
    }

    public List<AlbumResponse> findAllAlbumResponseStartingWith(String fragment){
        if(fragment == null || fragment.trim().isBlank()) return null;
        return albumRepository.findByTitleStartingWith(fragment).stream().map(albumResponseMapper::toAlbumResponse).toList();
    }


}
