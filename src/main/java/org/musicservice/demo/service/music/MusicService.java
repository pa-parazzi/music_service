package org.musicservice.demo.service.music;
import org.musicservice.demo.dto.music.mainResponse.AlbumResponse;
import org.musicservice.demo.dto.music.mainResponse.MainResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional(readOnly = true)
public class MusicService {

    private final AlbumService albumService;

    @Autowired
    public MusicService(AlbumService albumService) {
        this.albumService = albumService;
    }

    public MainResponse viewAlbums(){
        List<AlbumResponse> albumResponses = albumService.getAllAlbumResponse();
        MainResponse mainResponse =  new MainResponse();
        mainResponse.setAlbums(albumResponses);
        return mainResponse;
    }

    public AlbumResponse getById(Long albumId){
        return albumService.getAlbumById(albumId);
    }

}
