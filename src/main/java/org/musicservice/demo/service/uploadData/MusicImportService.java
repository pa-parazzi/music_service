package org.musicservice.demo.service.uploadData;

import jakarta.transaction.Transactional;
import org.musicservice.demo.dto.jamendo.MusicResponse;
import org.musicservice.demo.integration.jamendo.JamendoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;

@Service
public class MusicImportService {

    private final JamendoClient jamendoClient;
    private final MusicCatalogService musicCatalogService;

    @Autowired
    public MusicImportService(JamendoClient jamendoClient, MusicCatalogService musicCatalogService) {
        this.jamendoClient = jamendoClient;
        this.musicCatalogService = musicCatalogService;
    }

    @Transactional
    public void upload() {
        List<MusicResponse> musicResponseList = jamendoClient.getMusicData();
        for (MusicResponse response : musicResponseList) {
            String mp3Key = Normalizer.normalize(response.getName(), Normalizer.Form.NFD)
                    .replaceAll("\\p{M}", "")
                    .replaceAll("[^a-zA-Z0-9]", "_")
                    .replaceAll("_+", "_")
                    .replaceAll("^_+|_+$", "") + ".mp3";

            String imgKey = "albums/" + Normalizer.normalize(response.getAlbum_name(), Normalizer.Form.NFD)
                    .replaceAll("\\p{M}", "")
                    .replaceAll("[^a-zA-Z0-9]", "_")
                    .replaceAll("_+", "_")
                    .replaceAll("^_+|_+$", "") + ".jpg";
            response.setMp3Key(mp3Key);
            response.setAlbumImgKey(imgKey);
            musicCatalogService.insertMusicData(response);
        }
    }
}
