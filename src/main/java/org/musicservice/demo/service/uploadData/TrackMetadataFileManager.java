package org.musicservice.demo.service.uploadData;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.musicservice.demo.dto.metadata.TrackMetadata;
import org.musicservice.demo.exception.importer.WriteTrackMetadataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Component
public class TrackMetadataFileManager implements TrackMetadataWriter {

    private final ObjectMapper objectMapper;
    private final Path path;

    @Autowired
    public TrackMetadataFileManager(ObjectMapper objectMapper, @Value("${app.metadata-file}") String filePath) {
        this.objectMapper = objectMapper;
        this.path = Paths.get(filePath);
    }

    @PostConstruct
    private void initFile() {
        try {
            Path parent = path.getParent();
            if (parent!=null) {
                Files.createDirectories(parent);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось создать директорию");
        }
    }

    @Override
    public void write(TrackMetadata metadata){
        try{
            String json = objectMapper.writeValueAsString(metadata);
            Files.writeString(path, json + System.lineSeparator(),
                    StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e){
            throw new WriteTrackMetadataException("Ошибка при записи мета данных", e);
        }
    }
}
