package org.musicservice.demo.storage.s3;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public class MultipartFileUploadS3ObjectAdapter implements UploadS3Object{

    private final MultipartFile multipartFile;

    public MultipartFileUploadS3ObjectAdapter(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
    }

    @Override
    public InputStream inputStream() throws IOException {
        return multipartFile.getInputStream();
    }

    @Override
    public String contentType() {
        return multipartFile.getContentType();
    }

    @Override
    public Long contentSize() {
        return multipartFile.getSize();
    }
}
