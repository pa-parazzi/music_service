package org.musicservice.demo.storage.s3;

public interface ObjectStorageService {
    void upload(String s3Key, String bucket, UploadS3Object uploadS3Object);
}
