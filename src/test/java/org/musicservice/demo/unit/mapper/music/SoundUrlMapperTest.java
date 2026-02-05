package org.musicservice.demo.unit.mapper.music;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.musicservice.demo.mapper.music.SoundUrlMapper;
import org.musicservice.demo.service.yandexCloud.properties.YandexStorageProperties;
import org.musicservice.demo.service.yandexCloud.s3config.S3UrlGenerator;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SoundUrlMapperTest {

    @Mock
    private YandexStorageProperties yandexStorageProperties;
    @Mock
    private S3UrlGenerator s3UrlGenerator;

    @InjectMocks
    private SoundUrlMapper soundUrlMapper;

    @Test
    void mapUrl_ShouldGeneratePublicUrl(){
        String soundKey = "sound_key";
        String url = String.format("http://storage.mus-app-tracks/%s", soundKey);
        String bucketKey = "music";
        Map<String, String> soundBucketMap = Map.of(bucketKey, "mus-app-tracks");
        when(yandexStorageProperties.getBuckets()).thenReturn(soundBucketMap);
        when(s3UrlGenerator.generatePublicUrl(soundBucketMap.get(bucketKey), soundKey)).thenReturn(url);

        String result = soundUrlMapper.mapUrl(soundKey);

        assertEquals(url, result);
        verify(yandexStorageProperties).getBuckets();
        verify(s3UrlGenerator).generatePublicUrl(soundBucketMap.get(bucketKey), soundKey);
    }
}
