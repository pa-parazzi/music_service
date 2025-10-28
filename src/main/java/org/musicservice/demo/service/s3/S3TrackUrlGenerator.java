package org.musicservice.demo.service.s3;

import org.musicservice.demo.configuration.YandexCloud.YandexStorageProperties;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.net.URL;
import java.time.Duration;

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

    public String generatePresignedUrl(String bucket, String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15)) // 15 минут
                .getObjectRequest(getObjectRequest)
                .build();

        URL presignedUrl = presigner.presignGetObject(presignRequest).url();
        return presignedUrl.toString();
    }
}
