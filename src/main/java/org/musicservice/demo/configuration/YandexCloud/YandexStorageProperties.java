package org.musicservice.demo.configuration.YandexCloud;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;
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
