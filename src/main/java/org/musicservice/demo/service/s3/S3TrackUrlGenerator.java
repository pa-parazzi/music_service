package org.musicservice.demo.service.s3;

import org.musicservice.demo.configuration.YandexCloud.YandexStorageProperties;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3TrackUrlGenerator {

    private final S3Presigner presigner;
    private final String bucketName;

    public S3TrackUrlGenerator(YandexStorageProperties properties) {
        AwsBasicCredentials creds = AwsBasicCredentials.create(properties.getAccessKey(), properties.getSecretKey());

        this.presigner = S3Presigner.builder()
                .region(Region.of("ru-central1")) // регион Яндекса
                .credentialsProvider(StaticCredentialsProvider.create(creds))
                .endpointOverride(java.net.URI.create("https://storage.yandexcloud.net")) // важно
                .build();

        this.bucketName = properties.getBuckets().get("music");
    }

    public String generatePublicUrl(String bucket, String key) {
        return String.format("https://%s.storage.yandexcloud.net/%s", bucket, key);
    }
}
