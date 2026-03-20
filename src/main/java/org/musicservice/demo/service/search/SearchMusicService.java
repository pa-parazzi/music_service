package org.musicservice.demo.service.search;

import org.musicservice.demo.dto.music.album.AlbumResponse;
import org.musicservice.demo.dto.music.album.AlbumsResponse;
import org.musicservice.demo.dto.music.artist.ArtistResponse;
import org.musicservice.demo.dto.music.artist.ArtistsResponse;
import org.musicservice.demo.dto.music.search.SearchMusicResponse;
import org.musicservice.demo.dto.music.sound.SoundResponse;
import org.musicservice.demo.dto.music.sound.TracksResponse;
import org.musicservice.demo.exception.music.NoSuchMusicResultException;
import org.musicservice.demo.mapper.music.AlbumMapper;
import org.musicservice.demo.mapper.music.SoundMapper;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.musicservice.demo.repository.music.ArtistRepository;
import org.musicservice.demo.repository.music.SoundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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

    public SearchMusicResponse searchMusicResult(String fragment){
        if(isEmpty(fragment)) throw new NoSuchMusicResultException("Ничего не найдено");
        List<SoundResponse> tracks = getTracksByTitleStartingWith(fragment);
        List<AlbumResponse> albums = getAlbumsByTitleStartingWith(fragment);
        List<ArtistResponse> artists = getArtistsByNameStartingWith(fragment);
        return new SearchMusicResponse(tracks, albums, artists);
    }

    private List<SoundResponse> getTracksByTitleStartingWith(String fragment){
        return soundRepository.findAllByTitleStartingWithIgnoreCase(fragment.toLowerCase(), PageRequest.ofSize(5))
                .stream().map(soundMapper::toResponse).toList();
    }

    private List<AlbumResponse> getAlbumsByTitleStartingWith(String fragment){
        return albumRepository.findAllByTitleStartingWithIgnoreCase(fragment.toLowerCase(), PageRequest.ofSize(5))
                .stream().map(albumMapper::toAlbumResponse).toList();
    }

    private List<ArtistResponse> getArtistsByNameStartingWith(String fragment){
        return artistRepository.findAllByNameStartingWithIgnoreCase(fragment.toLowerCase(), PageRequest.ofSize(5))
                .stream().toList();
    }

    public ArtistsResponse getAllFoundArtists(String fragment){
        List<ArtistResponse> artists = artistRepository.findAllByNameStartingWithIgnoreCase(fragment.toLowerCase());
        return new ArtistsResponse(artists);
    }

    public AlbumsResponse getAllFoundAlbums(String fragment){
        List<AlbumResponse> albums = albumRepository.findAllByTitleStartingWithIgnoreCase(fragment.toLowerCase()).stream().map(albumMapper::toAlbumResponse).toList();
        return new AlbumsResponse(albums);
    }

    public TracksResponse getAllFoundTracks(String fragment){
        List<SoundResponse> tracks = soundRepository.findAllByTitleStartingWithIgnoreCase(fragment.toLowerCase()).stream().map(soundMapper::toResponse).toList();
        return new TracksResponse(tracks);
    }

    private boolean isEmpty(String fragment){
        return fragment == null || fragment.trim().isEmpty();
    }
}
