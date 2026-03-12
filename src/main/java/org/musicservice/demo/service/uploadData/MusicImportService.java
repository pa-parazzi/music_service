package org.musicservice.demo.service.uploadData;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.huxhorn.sulky.ulid.ULID;
import jakarta.annotation.PostConstruct;
import org.musicservice.demo.dto.metadata.TrackMetadata;
import org.musicservice.demo.entity.music.Genre;
import org.musicservice.demo.integration.jamendo.JamendoClient;
import org.musicservice.demo.integration.jamendo.response.MusicResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Service
public class MusicImportService {

    private final MusicCatalogService musicCatalogService;
    private final JamendoClient jamendoClient;
    private final ObjectMapper objectMapper;

    private static final ULID ulid = new ULID();
    private final Set<String> hashSet = new HashSet<>();

    @Value("${app.metadata-file}")
    private String filePath;

    @Autowired
    public MusicImportService(MusicCatalogService musicCatalogService, JamendoClient jamendoClient, ObjectMapper objectMapper) {
        this.musicCatalogService = musicCatalogService;
        this.jamendoClient = jamendoClient;
        this.objectMapper = objectMapper;
    }

    public void uploadData(String genreName) {
        Genre genre = musicCatalogService.findGenreByName(genreName);
        List<MusicResponse> responseList = filterMusicResponse(jamendoClient.tracksPack(genreName));
        for (MusicResponse response : responseList) {
            String albumImgKey = generateUploadAlbumImageKey();
            String mp3Key = generateUploadMp3Key();
            response.setAlbumImgKey(albumImgKey);
            response.setMp3Key(mp3Key);
            TrackMetadata trackMetadata = musicCatalogService.saveMusicData(response, genre);
            if(trackMetadata!=null){
                appendMetadataInFile(trackMetadata);
            }
        }
    }

    private List<MusicResponse> filterMusicResponse(List<MusicResponse> responseList){
        return responseList.stream()
                .filter(response -> response.getAudiodownload_allowed() == true)
                .filter(response -> response.getDuration() > 0)
                .toList();
    }

    private String generateUploadAlbumImageKey(){
        return "albums/" + generateULID() + ".jpg";
    }

    private String generateUploadMp3Key(){
        return generateULID() + ".mp3";
    }

    private String generateULID(){
        return ulid.nextULID();
    }

    private void appendMetadataInFile(TrackMetadata trackMetadata){
        if(hashSet.contains(trackMetadata.audiodownload())) throw new RuntimeException("Запись уже существует в файле");
        try{
            Path path = Paths.get(filePath);
            if(!Files.exists(path.getParent())){
                Files.createDirectories(path.getParent());
            }
            String json = objectMapper.writeValueAsString(trackMetadata);
            Files.writeString(path, json + System.lineSeparator(), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            hashSet.add(trackMetadata.audiodownload());
        } catch (IOException e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @PostConstruct
    private void initHashSetAudiodownloadValue(){
        try(Stream<String> lines = Files.lines(Path.of(filePath), StandardCharsets.UTF_8)) {
            lines.forEach(line -> {
                try {
                    JsonNode node = objectMapper.readTree(line);
                    hashSet.add(node.get("audiodownload").asText());
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
