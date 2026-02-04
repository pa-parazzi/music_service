package org.musicservice.demo.exception;

import java.io.IOException;

public class UploadObjectStorageException extends RuntimeException {
    public UploadObjectStorageException(String message) {
        super(message);
    }
}
