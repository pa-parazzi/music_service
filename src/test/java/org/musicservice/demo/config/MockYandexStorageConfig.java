package org.musicservice.demo.config;

import org.musicservice.demo.cloud.CloudStorageClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class MockYandexStorageConfig {

    @Bean
    public CloudStorageClient cloudStorageClient(){
        return new MockYandexStorageClient();
    }
}
