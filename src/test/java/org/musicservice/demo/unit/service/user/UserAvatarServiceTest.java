package org.musicservice.demo.unit.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.musicservice.demo.entity.image.UserAvatar;
import org.musicservice.demo.entity.user.User;
import org.musicservice.demo.repository.image.UserAvatarRepository;
import org.musicservice.demo.service.user.UserAvatarService;
import org.musicservice.demo.storage.s3.YandexStorageProperties;
import org.musicservice.demo.storage.s3.ObjectStorageService;
import org.musicservice.demo.storage.s3.MultipartFileUploadS3ObjectAdapter;
import org.musicservice.demo.support.factory.unit.user.UserDataFactory;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserAvatarServiceTest {

    @Mock
    private UserAvatarRepository userAvatarRepository;
    @Mock
    private ObjectStorageService objectStorageService;
    @Mock
    private YandexStorageProperties yandexStorageProperties;

    @InjectMocks
    private UserAvatarService userAvatarService;

    @Mock
    private final MockMultipartFile mockMultipartFile = new MockMultipartFile(
            "avatar",
            "avatar.png",
            "image/png",
            "fake image bytes".getBytes());

    @Test
    void create_ShouldReturnsNewUserAvatarWithNewKey_WhenFileIsPresent(){
        User user = UserDataFactory.user();
        String bucketKey = "img";
        Map<String, String> bucket = Map.of(bucketKey, "mus-app-img");
        when(yandexStorageProperties.getBuckets()).thenReturn(bucket);
        when(userAvatarRepository.save(any(UserAvatar.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserAvatar userAvatar = userAvatarService.create(mockMultipartFile, user);

        assertNotNull(userAvatar.getKey());
        assertEquals(user.getId(), userAvatar.getUser().getId());
        verifyNoMoreInteractions(yandexStorageProperties);
        verify(objectStorageService).upload(any(String.class), eq(bucket.get(bucketKey)),
                any(MultipartFileUploadS3ObjectAdapter.class));
    }

    @Test
    void create_ShouldReturnsNewUserAvatarWithDefaultKey_WhenFileIsNull(){
        User user = UserDataFactory.user();
        String defaultKey = "default_avatar.jpg";
        String bucketKey = "img";
        Map<String, String> bucket = Map.of(bucketKey, "mus-app-img");
        when(yandexStorageProperties.getDefaultAvatarKey()).thenReturn(defaultKey);
        when(yandexStorageProperties.getBuckets()).thenReturn(bucket);
        when(userAvatarRepository.save(any(UserAvatar.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserAvatar userAvatar = userAvatarService.create(null, user);

        assertEquals(defaultKey, userAvatar.getKey());
        assertEquals(user.getId(), userAvatar.getUser().getId());
        verify(yandexStorageProperties).getDefaultAvatarKey();
        verify(objectStorageService).upload(eq(defaultKey), eq(bucket.get(bucketKey)), any());
    }


}
