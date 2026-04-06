package org.musicservice.demo.service.search;

import org.musicservice.demo.dto.music.album.AlbumResponse;
import org.musicservice.demo.dto.music.artist.ArtistResponse;
import org.musicservice.demo.dto.music.common.PageResponse;
import org.musicservice.demo.dto.music.sound.SoundResponse;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.entity.music.Sound;
import org.musicservice.demo.mapper.music.AlbumMapper;
import org.musicservice.demo.mapper.music.SoundMapper;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.musicservice.demo.repository.music.ArtistRepository;
import org.musicservice.demo.repository.music.SoundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SearchMusicService {

    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;
    private final SoundRepository soundRepository;
    private final SoundMapper soundMapper;

    @Autowired
    public SearchMusicService(ArtistRepository artistRepository, AlbumRepository albumRepository, AlbumMapper albumMapper, SoundRepository soundRepository, SoundMapper soundMapper) {
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
        this.albumMapper = albumMapper;
        this.soundRepository = soundRepository;
        this.soundMapper = soundMapper;
    }

    public PageResponse<SoundResponse> getTracksByTitleStartingWith(String fragment, int page, int size){
        Page<Sound> pageResponse =  soundRepository.findByTitleStartingWithIgnoreCase
                (fragment, PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id")));
        List<SoundResponse> soundResponseList = pageResponse.getContent().stream().map(soundMapper::toResponse).toList();
        return new PageResponse<>(soundResponseList, pageResponse.hasNext());
    }

    public PageResponse<AlbumResponse> getAlbumsByTitleStartingWith(String fragment, int page, int size){
        Page<Album> pageResponse = albumRepository.findByTitleStartingWithIgnoreCase
                (fragment, PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id")));
        List<AlbumResponse> albumResponseList = pageResponse.getContent().stream().map(albumMapper::toAlbumResponse).toList();
        return new PageResponse<>(albumResponseList, pageResponse.hasNext());
    }

    public PageResponse<ArtistResponse> getArtistsByNameStartingWith(String fragment, int page, int size){
        Page<Artist> pageResponse = artistRepository.findByNameStartingWithIgnoreCase
                (fragment, PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id")));
        List<ArtistResponse> artistResponseList = pageResponse.getContent()
                .stream().map(artist -> new ArtistResponse(artist.getId(), artist.getName())).toList();
        return new PageResponse<>(artistResponseList, pageResponse.hasNext());
    }

}
