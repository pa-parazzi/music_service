package org.musicservice.demo.mapper.image;

import org.mapstruct.Named;
import org.musicservice.demo.configuration.YandexCloud.YandexStorageProperties;
import org.musicservice.demo.service.s3.S3UrlGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AlbumImageUrlMapper {

    private final YandexStorageProperties yandexStorageProperties;
    private final S3UrlGenerator s3UrlGenerator;

    @Autowired
    public AlbumImageUrlMapper(YandexStorageProperties yandexStorageProperties, S3UrlGenerator s3UrlGenerator) {
        this.yandexStorageProperties = yandexStorageProperties;
        this.s3UrlGenerator = s3UrlGenerator;
    }

    @Named("mapUrl")
    public String mapUrl(String s3Key){
        return s3UrlGenerator.generatePublicUrl(yandexStorageProperties.getBuckets().get("img"), s3Key);
    }
}
