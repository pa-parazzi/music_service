package org.musicservice.demo.mapper.image;

import org.mapstruct.Named;
import org.musicservice.demo.storage.s3.YandexStorageProperties;
import org.musicservice.demo.storage.s3.S3UrlGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ImageUrlMapper {

    private final YandexStorageProperties yandexStorageProperties;

    @Autowired
    public ImageUrlMapper(YandexStorageProperties yandexStorageProperties) {
        this.yandexStorageProperties = yandexStorageProperties;
    }

    @Named("mapImgUrl")
    public String mapUrl(String key){
        return S3UrlGenerator.generatePublicUrl(yandexStorageProperties.getBuckets().get("img"), key);
    }
}
