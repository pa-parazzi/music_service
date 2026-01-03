package org.musicservice.demo.service.search;

import org.musicservice.demo.dto.music.album.AlbumResponse;
import org.musicservice.demo.mapper.music.AlbumMapper;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SearchAlbumService {

    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;

    @Autowired
    public SearchAlbumService(AlbumRepository albumRepository, AlbumMapper albumMapper) {
        this.albumRepository = albumRepository;
        this.albumMapper = albumMapper;
    }

    public List<AlbumResponse> findAllAlbumResponseStartingWith(String fragment){
        if(fragment == null || fragment.trim().isBlank()) return null;
        return albumRepository.findByTitleStartingWith(fragment).stream().map(albumMapper::toAlbumResponse).toList();
    }


}
