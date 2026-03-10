package org.musicservice.demo.service.uploadData;

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
    public MusicCatalogService(ArtistRepository artistRepository, AlbumRepository albumRepository, SoundRepository soundRepository, AlbumImageRepository albumImageRepository, GenreRepository genreRepository) {
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
        this.soundRepository = soundRepository;
        this.albumImageRepository = albumImageRepository;
        this.genreRepository = genreRepository;
    }

    public Artist getArtist(MusicResponse response) {
        return artistRepository.findByName(response.getArtist_name()).orElseGet(() -> {
            Artist artist = new Artist();
            artist.setName(response.getArtist_name());
            return artist;
        });
    }

    public Album getAlbum(MusicResponse response) {
        return albumRepository.findByTitle(response.getAlbum_name())
                .orElseGet(() -> {
                    Album newAlbum = new Album();
                    newAlbum.setTitle(response.getAlbum_name());
                    return newAlbum;
                });
    }

    public AlbumImage getAlbumImage(MusicResponse response) {
        return albumImageRepository.findByKey(response.getAlbumImgKey())
                .orElseGet(() -> {
                    AlbumImage albumImage = new AlbumImage();
                    albumImage.setKey(response.getAlbumImgKey());
                    return albumImage;
                });
    }

    public Sound getSound(MusicResponse response) {
        return soundRepository.findByTitle(response.getName())
                .orElseGet(() -> {
                    Sound newSound = new Sound();
                    newSound.setTitle(response.getName());
                    newSound.setDuration(response.getDuration());
                    newSound.setKey(response.getMp3Key());
                    newSound.setReleaseDate(response.getReleasedate());
                    return newSound;
                });
    }

    public Genre genre(String genreName){
        return genreRepository.findByName(GenreName.valueOf(genreName)).orElseThrow();
    }

    @Transactional
    public void saveMusicData(MusicResponse response, String genreName) {
        Artist artist = getArtist(response);

        Album album = getAlbum(response);
        album.setArtist(artist);

        AlbumImage albumImage = getAlbumImage(response);
        album.setImage(albumImage);

        Genre genre = genre(genreName);

        Sound sound = getSound(response);
        sound.setArtist(artist);
        sound.setAlbum(album);
        sound.setGenre(genre);

        artistRepository.save(artist);
        albumRepository.save(album);
        soundRepository.save(sound);
    }

}
