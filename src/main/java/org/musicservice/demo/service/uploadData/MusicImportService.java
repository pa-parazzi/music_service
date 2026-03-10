package org.musicservice.demo.service.uploadData;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.musicservice.demo.dto.metadata.TrackMetadata;
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
import java.text.Normalizer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Service
public class MusicImportService {

    private final MusicCatalogService musicCatalogService;
    private final JamendoClient jamendoClient;
    private final ObjectMapper objectMapper;

    @Value("${app.metadata-file}")
    private String filePath;

    @Autowired
    public MusicImportService(MusicCatalogService musicCatalogService, JamendoClient jamendoClient, ObjectMapper objectMapper) {
        this.musicCatalogService = musicCatalogService;
        this.jamendoClient = jamendoClient;
        this.objectMapper = objectMapper;
    }

    public void uploadData(String genreName) {
        List<MusicResponse> responseList = filterByAudiodownload_allowed(jamendoClient.tracksPack(genreName));
        for (MusicResponse response : responseList) {
            String albumImgKey = generateUploadAlbumImageKey(response);
            String trackKey = generateUploadMp3Key(response);
            response.setAlbumImgKey(albumImgKey);
            response.setMp3Key(trackKey);
            musicCatalogService.saveMusicData(response, genreName);
            appendMetadataInFile(response);
        }
    }

    private List<MusicResponse> filterByAudiodownload_allowed(List<MusicResponse> responseList){
        return responseList.stream().filter(response -> response.getAudiodownload_allowed() == true).toList();
    }

    private String generateUploadAlbumImageKey(MusicResponse response){
        return "albums/" + Normalizer.normalize(response.getAlbum_name(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^a-zA-Z0-9]", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_+|_+$", "") + ".jpg";
    }

    private String generateUploadMp3Key(MusicResponse response){
        return Normalizer.normalize(response.getName(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^a-zA-Z0-9]", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_+|_+$", "") + ".mp3";
    }

    private void appendMetadataInFile(MusicResponse response){
        if(checkMetadataExist(response)) return;
        try{
            Path path = Paths.get(filePath);
            if(!Files.exists(path.getParent())){
                Files.createDirectories(path.getParent());
            }
            TrackMetadata trackMetadata = trackMetadata(response);
            String json = objectMapper.writeValueAsString(trackMetadata);
            Files.writeString(path, json + System.lineSeparator(), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e){
            throw new RuntimeException(e.getMessage());
        }
    }

    private TrackMetadata trackMetadata(MusicResponse response){
        return new TrackMetadata(response.getName(), response.getAlbum_name(),
                response.getAlbum_image(), response.getAudiodownload(),
                response.getMp3Key(), response.getAlbumImgKey());
    }

    private boolean checkMetadataExist(MusicResponse response){
        Set<String> hashSet = new HashSet<>();
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
        return hashSet.contains(response.getAudiodownload());
    }

}
