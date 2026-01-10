package org.musicservice.demo.mapper.image;

import org.mapstruct.Named;
import org.musicservice.demo.service.yandexCloud.properties.YandexStorageProperties;
import org.musicservice.demo.service.yandexCloud.s3config.S3UrlGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserAvatarUrlMapper {

    private final YandexStorageProperties yandexStorageProperties;
    private final S3UrlGenerator s3UrlGenerator;

    @Autowired
    public UserAvatarUrlMapper(YandexStorageProperties yandexStorageProperties, S3UrlGenerator s3UrlGenerator) {
        this.yandexStorageProperties = yandexStorageProperties;
        this.s3UrlGenerator = s3UrlGenerator;
    }

    @Named("mapUrl")
    public String mapUrl(String key){
        return s3UrlGenerator.generatePublicUrl(yandexStorageProperties.getBuckets().get("img"), key);
    }
}
