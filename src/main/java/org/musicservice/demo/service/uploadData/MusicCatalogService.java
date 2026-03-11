package org.musicservice.demo.service.uploadData;

import org.musicservice.demo.dto.metadata.TrackMetadata;
import org.musicservice.demo.entity.image.AlbumImage;
import org.musicservice.demo.entity.music.*;
import org.musicservice.demo.integration.jamendo.response.MusicResponse;
import org.musicservice.demo.repository.image.AlbumImageRepository;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.musicservice.demo.repository.music.ArtistRepository;
import org.musicservice.demo.repository.music.GenreRepository;
import org.musicservice.demo.repository.music.SoundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MusicCatalogService {

    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final SoundRepository soundRepository;
    private final AlbumImageRepository albumImageRepository;
    private final GenreRepository genreRepository;

    @Autowired
    public MusicCatalogService(ArtistRepository artistRepository, AlbumRepository albumRepository,
                               SoundRepository soundRepository, AlbumImageRepository albumImageRepository,
                               GenreRepository genreRepository) {
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
        this.soundRepository = soundRepository;
        this.albumImageRepository = albumImageRepository;
        this.genreRepository = genreRepository;
    }

    public Artist createOrGetArtist(MusicResponse response) {
        return artistRepository.findByName(response.getArtist_name())
                .orElseGet(() -> artistRepository.save(new Artist(response.getArtist_name())));
    }

    public Album createAlbumWithImageOrGet(MusicResponse response, Artist artist) {
        Album album = albumRepository.findByTitle(response.getAlbum_name())
                .orElseGet(() -> albumRepository.save(new Album(response.getAlbum_name(), artist)));
        AlbumImage albumImage = albumImageRepository.findByKey(response.getAlbumImgKey())
                .orElseGet(() -> albumImageRepository.save(new AlbumImage(response.getAlbumImgKey(), album)));
        album.setImage(albumImage);
        return album;
    }

    public void createSound(MusicResponse response, Artist artist, Album album, Genre genre) {
        soundRepository.save(new Sound(response.getName(), response.getDuration(),
                            artist, album, response.getMp3Key(),
                            response.getReleasedate(), genre));
    }

    public Genre findGenreByName(String genreName){
        return genreRepository.findByName(GenreName.valueOf(genreName)).orElseThrow();
    }

    @Transactional
    public TrackMetadata saveMusicData(MusicResponse response, Genre genre) {
        if(soundRepository.existsByKey(response.getMp3Key())) return null;
        Artist artist = createOrGetArtist(response);
        Album album = createAlbumWithImageOrGet(response, artist);
        createSound(response, artist, album, genre);
        return new TrackMetadata(response.getName(), response.getAlbum_name(),
                response.getAlbum_image(), response.getAudiodownload(),
                response.getMp3Key(), response.getAlbumImgKey());
    }

}
