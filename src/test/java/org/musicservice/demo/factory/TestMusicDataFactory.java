package org.musicservice.demo.factory;

import org.musicservice.demo.cloud.CloudStorageClient;
import org.musicservice.demo.config.MockYandexStorageConfig;
import org.musicservice.demo.configuration.YandexCloud.YandexStorageProperties;
import org.musicservice.demo.dto.image.AlbumImageDto;
import org.musicservice.demo.dto.music.ArtistDto;
import org.musicservice.demo.dto.music.SoundDto;
import org.musicservice.demo.dto.music.mainResponse.AlbumResponse;
import org.musicservice.demo.mapper.image.AlbumImageMapper;
import org.musicservice.demo.mapper.music.ArtistMapper;
import org.musicservice.demo.mapper.music.SoundMapper;
import org.musicservice.demo.model.image.AlbumImage;
import org.musicservice.demo.model.music.Album;
import org.musicservice.demo.model.music.Artist;
import org.musicservice.demo.model.music.Sound;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.musicservice.demo.repository.music.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@TestConfiguration
@Import(MockYandexStorageConfig.class)
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
    private ArtistMapper artistMapper;

    @Autowired
    private AlbumImageMapper albumImageMapper;

    @Autowired
    private SoundMapper soundMapper;

    @Autowired
    private CloudStorageClient cloudStorageClient;

    @Autowired
    private YandexStorageProperties yandexStorageProperties;

    public void cleanData(){
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

    public AlbumResponse getAlbumResponseByFactoryMusicData(Album album){
        String albumImgUrl = cloudStorageClient.createPublicUrl(yandexStorageProperties.getBuckets().get("img"), album.getImage().getS3Key());

        AlbumImageDto albumImageDto = albumImageMapper.convertToDto(album.getImage());
        albumImageDto.setUrl(albumImgUrl);

        ArtistDto artistDto = artistMapper.toDto(album.getArtist());

        List<SoundDto> soundDtoList = album.getSoundList().stream().map(sound -> {
            String trackUrl = cloudStorageClient.createPublicUrl(yandexStorageProperties.getBuckets().get("music"), sound.getKey());
            SoundDto soundDto = soundMapper.toDto(sound);
            soundDto.setUrl(trackUrl);
            return soundDto;
        }).toList();

        AlbumResponse albumResponse = new AlbumResponse();
        albumResponse.setAlbumId(album.getId());
        albumResponse.setTitle(album.getTitle());
        albumResponse.setAlbumImage(albumImageDto);
        albumResponse.setArtist(artistDto);
        albumResponse.setSoundList(soundDtoList);

        return albumResponse;
    }
}
