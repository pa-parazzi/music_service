package org.musicservice.demo.support.factory.it.multipartFile;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class MultipartFileFactory {

    public static MockMultipartFile userPart(String json){
        return new MockMultipartFile(
                "user",
                "user.json",
                MediaType.APPLICATION_JSON_VALUE,
                json.getBytes(StandardCharsets.UTF_8));
    }

    public static MockMultipartFile imagePart() throws IOException {
        ClassPathResource resource = new ClassPathResource("image/test_avatar_image.jpg");
        String fileName = resource.getFilename();
        byte[] image = resource.getInputStream().readAllBytes();
        return new MockMultipartFile(
                "file",
                fileName,
                MediaType.IMAGE_JPEG_VALUE,
                image);
    }
}
