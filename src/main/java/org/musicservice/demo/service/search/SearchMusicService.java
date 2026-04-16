package org.musicservice.demo.service.search;

import org.musicservice.demo.dto.music.album.AlbumResponse;
import org.musicservice.demo.dto.music.artist.ArtistResponse;
import org.musicservice.demo.dto.music.common.PageResponse;
import org.musicservice.demo.dto.music.sound.SoundResponse;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.entity.music.Sound;
import org.musicservice.demo.exception.music.NoSuchMusicException;
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
    public SearchMusicService(
            ArtistRepository artistRepository, AlbumRepository albumRepository,
            AlbumMapper albumMapper, SoundRepository soundRepository,
            SoundMapper soundMapper) {
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
        this.albumMapper = albumMapper;
        this.soundRepository = soundRepository;
        this.soundMapper = soundMapper;
    }

    public PageResponse<SoundResponse> getTracksByTitleStartingWith(String fragment, int page, int size){
        Page<Sound> soundsPage =  soundRepository.findByTitleStartingWithIgnoreCase
                (fragment, PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id")));
        List<Sound> sounds = checkForEmptyContent(soundsPage,
                "Треки по запросу " + "\"" + fragment + "\"" + " не найдены");
        List<SoundResponse> soundResponseList = sounds.stream().map(soundMapper::toResponse).toList();
        return new PageResponse<>(soundResponseList, soundsPage.hasNext());
    }

    public PageResponse<AlbumResponse> getAlbumsByTitleStartingWith(String fragment, int page, int size){
        Page<Album> albumsPage = albumRepository.findByTitleStartingWithIgnoreCase
                (fragment, PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id")));
        List<Album> albums = checkForEmptyContent(albumsPage,
                "Альбомы по запросу " + "\"" + fragment + "\"" + " не найдены");
        List<AlbumResponse> albumResponseList = albums.stream().map(albumMapper::toAlbumResponse).toList();
        return new PageResponse<>(albumResponseList, albumsPage.hasNext());
    }

    public PageResponse<ArtistResponse> getArtistsByNameStartingWith(String fragment, int page, int size){
        Page<Artist> artistsPage = artistRepository.findByNameStartingWithIgnoreCase
                (fragment, PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id")));
        List<Artist> artists = checkForEmptyContent(artistsPage,
                "Испольнители по запросу " + "\"" + fragment + "\"" + " не найдены");
        List<ArtistResponse> artistResponseList = artists.stream().map
                (artist -> new ArtistResponse(artist.getId(), artist.getName())).toList();
        return new PageResponse<>(artistResponseList, artistsPage.hasNext());
    }

    private <T> List<T> checkForEmptyContent(Page<T> paging, String exceptionMessage){
        List<T> content = paging.getContent();
        if(content.isEmpty()) throw new NoSuchMusicException(exceptionMessage);
        return content;
    }
}
