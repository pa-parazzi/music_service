package org.musicservice.demo.mapper.music;

import org.mapstruct.Named;
import org.musicservice.demo.service.yandexCloud.properties.YandexStorageProperties;
import org.musicservice.demo.service.yandexCloud.s3.S3UrlGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SoundUrlMapper {

    private final YandexStorageProperties yandexStorageProperties;

    @Autowired
    public SoundUrlMapper(YandexStorageProperties yandexStorageProperties) {
        this.yandexStorageProperties = yandexStorageProperties;
    }

    @Named("mapUrl")
    public String mapUrl(String key){
        return S3UrlGenerator.generatePublicUrl(yandexStorageProperties.getBuckets().get("music"), key);
    }
}
