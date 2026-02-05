package org.musicservice.demo.unit.mapper.image;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.musicservice.demo.mapper.image.ImageUrlMapper;
import org.musicservice.demo.service.yandexCloud.properties.YandexStorageProperties;
import org.musicservice.demo.service.yandexCloud.s3config.S3UrlGenerator;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ImageUrlMapperTest {

    @Mock
    private YandexStorageProperties yandexStorageProperties;
    @Mock
    private S3UrlGenerator s3UrlGenerator;

    @InjectMocks
    private ImageUrlMapper imageUrlMapper;

    @Test
    void mapUrl_ShouldGeneratePublicUrl(){
        String imgKey = "image_key";
        String url = String.format("http://storage.mus-app-image/%s", imgKey);
        String bucketKey = "img";
        Map<String, String> imgBucketMap = Map.of(bucketKey, "mus-app-image");
        when(yandexStorageProperties.getBuckets()).thenReturn(imgBucketMap);
        when(s3UrlGenerator.generatePublicUrl(imgBucketMap.get(bucketKey), imgKey)).thenReturn(url);

        String result = imageUrlMapper.mapUrl(imgKey);

        assertEquals(url, result);
        verify(yandexStorageProperties).getBuckets();
        verify(s3UrlGenerator).generatePublicUrl(imgBucketMap.get(bucketKey), imgKey);
    }



}
