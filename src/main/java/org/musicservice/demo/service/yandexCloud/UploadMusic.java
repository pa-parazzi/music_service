package org.musicservice.demo.service.yandexCloud;

import org.musicservice.demo.dto.jamendo.UploadMusicResponse;
import org.musicservice.demo.entity.image.AlbumImage;
import org.musicservice.demo.entity.music.Album;
import org.musicservice.demo.entity.music.Artist;
import org.musicservice.demo.entity.music.Sound;
import org.musicservice.demo.repository.image.AlbumImageRepository;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.musicservice.demo.repository.music.ArtistRepository;
import org.musicservice.demo.repository.music.SoundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UploadMusic {

    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final SoundRepository soundRepository;
    private final AlbumImageRepository albumImageRepository;

    @Autowired
    public UploadMusic(ArtistRepository artistRepository, AlbumRepository albumRepository, SoundRepository soundRepository, AlbumImageRepository albumImageRepository) {
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
        this.soundRepository = soundRepository;
        this.albumImageRepository = albumImageRepository;
    }

    public Artist getArtist(UploadMusicResponse response) {
        return artistRepository.findByName(response.getArtist_name()).orElseGet(() -> {
            Artist artist = new Artist();
            artist.setName(response.getArtist_name());
            return artist;
        });
    }

    public Album getAlbum(UploadMusicResponse response) {
        return albumRepository.findByTitle(response.getAlbum_name())
                .orElseGet(() -> {
                    Album newAlbum = new Album();
                    newAlbum.setTitle(response.getAlbum_name());
                    return newAlbum;
                });
    }

    public AlbumImage getAlbumImage(UploadMusicResponse response) {
        return albumImageRepository.findByKey(response.getImgKey())
                .orElseGet(() -> {
                    AlbumImage albumImage = new AlbumImage();
                    albumImage.setKey(response.getImgKey());
                    return albumImage;
                });
    }

    public Sound getSound(UploadMusicResponse response) {
        return soundRepository.findByTitle(response.getName())
                .orElseGet(() -> {
                    Sound newSound = new Sound();
                    newSound.setTitle(response.getName());
                    newSound.setDuration(response.getDuration());
                    newSound.setKey(response.getMp3Key());
                    return newSound;
                });
    }

    @Transactional
    public void insertMusicData(UploadMusicResponse response) {
        Artist artist = getArtist(response);

        Album album = getAlbum(response);
        album.setArtist(artist);

        AlbumImage albumImage = getAlbumImage(response);
        albumImage.setAlbum(album);
        album.setImage(albumImage);

        Sound sound = getSound(response);
        sound.setArtist(artist);
        sound.setAlbum(album);


        if(!album.getSoundList().contains(sound)){
            album.getSoundList().add(sound);
        }

        if(!albumImage.getAlbum().equals(album)){
            albumImage.setAlbum(album);
        }

        artistRepository.save(artist);
        albumRepository.save(album);
    }

}
