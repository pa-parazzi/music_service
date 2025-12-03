package org.musicservice.demo.factory;

import org.musicservice.demo.dto.music.mainResponse.AlbumResponse;
import org.musicservice.demo.dto.music.mainResponse.MainResponse;
import org.musicservice.demo.mapper.music.AlbumResponseMapper;
import org.musicservice.demo.model.image.AlbumImage;
import org.musicservice.demo.model.music.Album;
import org.musicservice.demo.model.music.Artist;
import org.musicservice.demo.model.music.Sound;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.musicservice.demo.repository.music.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@TestConfiguration
public class TestMusicDataFactory {

    @Autowired
    private ArtistFactory artistFactory;

    @Autowired
    private AlbumFactory albumFactory;

    @Autowired
    private SoundFactory soundFactory;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private AlbumResponseMapper albumResponseMapper;

    public void cleanData(){
        artistRepository.deleteAll();
        albumRepository.deleteAll();
    }

    public Album createFactoryMusicData(){
        Artist artist = artistFactory.createFactory();
        Album album = albumFactory.createFactoryAlbum();
        String s3ImgKey = album.getTitle() + ".jpg";
        AlbumImage albumImage = new AlbumImage(s3ImgKey, album);
        List<Sound> soundList = soundFactory.createFactorySoundList(artist, album);

        artist.setAlbums(Collections.singletonList(album));
        artist.setSoundList(soundList);

        album.setImage(albumImage);
        album.setArtist(artist);
        album.setSoundList(soundList);

        soundList.forEach(sound -> {
            String s3TrackKey = String.format("%s/", sound.getAlbum().getTitle() + sound.getTitle() + ".mp3");
            sound.setKey(s3TrackKey);
        });

        artistRepository.save(artist);
        albumRepository.save(album);

        return album;
    }

    public MainResponse getFactoryMainResponse(Album album){
        MainResponse mainResponse = new MainResponse();
        List<AlbumResponse> albumResponses = new ArrayList<>();
        albumResponses.add(getAlbumResponseByFactoryMusicData(album));
        mainResponse.setAlbums(albumResponses);
        return mainResponse;
    }

    public AlbumResponse getAlbumResponseByFactoryMusicData(Album album){
        return albumResponseMapper.toAlbumResponse(album);
    }
}
