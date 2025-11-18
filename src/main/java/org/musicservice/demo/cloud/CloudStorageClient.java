package org.musicservice.demo.cloud;

import org.springframework.web.multipart.MultipartFile;

public interface CloudStorageClient {

    String createPublicUrl(String bucketName, String key);

    void delete(String key);
}
