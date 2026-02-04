package org.musicservice.demo.service.s3Storage;

import org.springframework.web.multipart.MultipartFile;


public interface ObjectStorage {
    String upload(MultipartFile file);
}
