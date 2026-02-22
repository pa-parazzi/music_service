package org.musicservice.demo.service.yandexCloud.s3;

import org.springframework.web.multipart.MultipartFile;

public interface ObjectStorageService {
    String upload(MultipartFile file);
}
