package org.musicservice.demo.service.search;

import org.musicservice.demo.repository.music.AlbumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SearchAlbumService {

    private final AlbumRepository albumRepository;

    @Autowired
    public SearchAlbumService(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

}
