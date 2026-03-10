package org.musicservice.demo.unit.mapper.music;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.musicservice.demo.mapper.music.SoundUrlMapper;
import org.musicservice.demo.storage.s3.YandexStorageProperties;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SoundUrlMapperTest {

    @Mock
    private YandexStorageProperties yandexStorageProperties;

    @InjectMocks
    private SoundUrlMapper soundUrlMapper;

    @Test
    void mapUrl_ShouldGenerateValidPublicUrl(){
        String bucketKey = "music";
        Map<String, String> bucket = Map.of(bucketKey, "mus-app-tracks");
        String soundKey = "sound_key";
        String url = String.format("https://%s.storage.yandexcloud.net/%s", bucket.get(bucketKey),soundKey);
        when(yandexStorageProperties.getBuckets()).thenReturn(bucket);

        String result = soundUrlMapper.mapUrl(soundKey);

        assertEquals(url, result);
        verify(yandexStorageProperties).getBuckets();
    }
}
