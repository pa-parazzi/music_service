package org.musicservice.demo.service.uploadData;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
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
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

@Component
public class MetadataFileWriter {

    private final ObjectMapper objectMapper;
    private final String filePath;

    private final Set<String> hashSet = new HashSet<>();

    @Autowired
    public MetadataFileWriter(ObjectMapper objectMapper, @Value("${app.metadata-file}") String filePath) {
        this.objectMapper = objectMapper;
        this.filePath = filePath;
    }

    public void appendMetadataInFile(TrackMetadata trackMetadata){
        if(hashSet.contains(trackMetadata.audiodownload())) return;
        try{
            Path path = Paths.get(filePath);
            if(!Files.exists(path.getParent())){
                Files.createDirectories(path.getParent());
            }
            String json = objectMapper.writeValueAsString(trackMetadata);
            Files.writeString(path, json + System.lineSeparator(),
                    StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            hashSet.add(trackMetadata.audiodownload());
        } catch (IOException e){
            throw new WriteTrackMetadataException
                    ("Ошибка загрузки мета данных в файл - " + e + ". Путь файла: " + filePath);
        }
    }

    @PostConstruct
    public void initHashSetAudiodownloadValue(){
        try(Stream<String> lines = Files.lines(Path.of(filePath), StandardCharsets.UTF_8)) {
            lines.forEach(line -> {
                try {
                    TrackMetadata trackMetadata = objectMapper.readValue(line, TrackMetadata.class);
                    hashSet.add(trackMetadata.audiodownload());
                } catch (JsonProcessingException e) {
                    throw new WriteTrackMetadataException
                            ("Ошибка при чтении файла - " + e + ". Путь файла: " + filePath);
                }
            });
        } catch (IOException e) {
            throw new WriteTrackMetadataException("Ошибка при чтении файла - " + e + ". Путь файла: " + filePath);
        }
    }

}
