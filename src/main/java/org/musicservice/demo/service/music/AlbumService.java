package org.musicservice.demo.service.music;

import org.musicservice.demo.configuration.YandexCloud.YandexStorageProperties;
import org.musicservice.demo.dto.image.AlbumImageDto;
import org.musicservice.demo.dto.music.SoundDto;
import org.musicservice.demo.dto.music.mainResponse.AlbumResponse;
import org.musicservice.demo.mapper.image.AlbumImageMapper;
import org.musicservice.demo.mapper.music.AlbumMapper;
import org.musicservice.demo.model.image.AlbumImage;
import org.musicservice.demo.repository.music.AlbumRepository;
import org.musicservice.demo.service.s3.S3ImgUrlGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final AlbumMapper albumMapper;
    private final AlbumImageMapper albumImageMapper;
    private final YandexStorageProperties yandexStorageProperties;
    private final S3ImgUrlGenerator s3ImgUrlGenerator;

    @Autowired
    public AlbumService(AlbumRepository albumRepository, AlbumMapper albumMapper, AlbumImageMapper albumImageMapper, YandexStorageProperties yandexStorageProperties, S3ImgUrlGenerator s3ImgUrlGenerator) {
        this.albumRepository = albumRepository;
        this.albumMapper = albumMapper;
        this.albumImageMapper = albumImageMapper;
        this.yandexStorageProperties = yandexStorageProperties;
        this.s3ImgUrlGenerator = s3ImgUrlGenerator;
    }

    public List<AlbumResponse> getAlbums(List<SoundDto> soundDtoList){
        return albumRepository.findAll().stream().map(album -> {
            AlbumImage albumImage = album.getImage();
            String url = s3ImgUrlGenerator.generatePublicUrl(yandexStorageProperties.getBuckets().get("img"), albumImage.getS3Key());
            AlbumImageDto imageDto = albumImageMapper.convertToDto(albumImage);
            AlbumResponse albumResponse = albumMapper.convertToAlbumResponse(album);
            imageDto.setKey(albumImage.getS3Key());
            imageDto.setUrl(url);
            albumResponse.setAlbumImage(imageDto);
            albumResponse.setSoundList(soundDtoList);
            return albumResponse;
        }).toList();
    }


}
