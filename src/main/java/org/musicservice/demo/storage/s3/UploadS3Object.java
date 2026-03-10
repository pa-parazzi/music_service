package org.musicservice.demo.storage.s3;

import java.io.IOException;
import java.io.InputStream;

public interface UploadS3Object {
    InputStream inputStream() throws IOException;
    String contentType();
    Long contentSize();
}
