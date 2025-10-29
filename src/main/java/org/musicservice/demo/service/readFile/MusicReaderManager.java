package org.musicservice.demo.service.readFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.musicservice.demo.dto.music.UploadMusicResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public final class MusicReaderManager {

    public static List<UploadMusicResponse> readToParse(MultipartFile file) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(file.getInputStream(), new TypeReference<List<UploadMusicResponse>>() {});
    }

}
