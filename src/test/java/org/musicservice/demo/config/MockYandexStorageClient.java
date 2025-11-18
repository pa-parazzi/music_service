package org.musicservice.demo.config;

import org.musicservice.demo.cloud.CloudStorageClient;
import org.musicservice.demo.configuration.YandexCloud.YandexStorageProperties;
import org.springframework.web.multipart.MultipartFile;

public class MockYandexStorageClient implements CloudStorageClient {

    private final String baseMockUrl;

    public MockYandexStorageClient() {
        this.baseMockUrl = "https://mock-storage.local/";
    }

    @Override
    public String createPublicUrl(String bucketName, String key) {
        return baseMockUrl + bucketName + "/" + key;
    }

    @Override
    public void delete(String key) {

    }
}
