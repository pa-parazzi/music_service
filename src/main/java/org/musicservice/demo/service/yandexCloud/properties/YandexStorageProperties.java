package org.musicservice.demo.service.yandexCloud.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "yandex")
@Getter
@Setter
public class YandexStorageProperties {

    private String accessKey;
    private String secretKey;
    private String region;
    private String endpoint;

    private Map<String, String> buckets;

    private String defaultAvatarKey;

}
