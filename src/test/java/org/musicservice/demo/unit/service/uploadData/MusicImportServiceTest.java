package org.musicservice.demo.unit.service.uploadData;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.musicservice.demo.dto.metadata.TrackMetadata;
import org.musicservice.demo.entity.genre.Genre;
import org.musicservice.demo.exception.music.GenreDoesNotExistException;
import org.musicservice.demo.integration.jamendo.JamendoClient;
import org.musicservice.demo.integration.jamendo.response.MusicResponse;
import org.musicservice.demo.service.uploadData.MetadataFileWriter;
import org.musicservice.demo.service.uploadData.MusicCatalogService;
import org.musicservice.demo.service.uploadData.MusicImportService;
import org.musicservice.demo.support.factory.unit.music.MusicDataFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MusicImportServiceTest {

    @Mock
    private MusicCatalogService musicCatalogService;
    @Mock
    private JamendoClient jamendoClient;
    @Mock
    private MetadataFileWriter metadataFileWriter;

    @InjectMocks
    private MusicImportService musicImportService;

    @Test
    void uploadData_ShouldThrowGenreDoesNotExistException(){
        String genreName = "NO NAME GENRE";
        when(musicCatalogService.findGenreByName(genreName))
                .thenThrow(new GenreDoesNotExistException("Жанр не существует"));

        assertThrows(GenreDoesNotExistException.class, () -> musicImportService.uploadData(genreName));

        verifyNoInteractions(jamendoClient);
        verifyNoMoreInteractions(musicCatalogService);
        verifyNoInteractions(metadataFileWriter);
    }

    @Test
    void uploadData_ShouldSaveAndAppendMusicDataInFile() {
        String genreName = "POP";
        Genre genre = new Genre();
        MusicResponse musicResponse = MusicDataFactory.musicResponse();
        TrackMetadata trackMetadata = MusicDataFactory.trackMetadata();

        when(musicCatalogService.findGenreByName(genreName)).thenReturn(genre);
        when(jamendoClient.tracksPack(genreName)).thenReturn(List.of(musicResponse));
        when(musicCatalogService.buildTrackMetadata(any(MusicResponse.class))).thenReturn(trackMetadata);

        musicImportService.uploadData(genreName);

        verify(jamendoClient).tracksPack(genreName);

        ArgumentCaptor<MusicResponse> musicResponseCaptor1 = ArgumentCaptor.forClass(MusicResponse.class);
        verify(musicCatalogService).saveMusicData(musicResponseCaptor1.capture(), eq(genre));
        MusicResponse responseFromSaveMusicData = musicResponseCaptor1.getValue();
        assertNotNull(responseFromSaveMusicData.mp3Key());
        assertNotNull(responseFromSaveMusicData.albumImgKey());

        ArgumentCaptor<MusicResponse> musicResponseCaptor2 = ArgumentCaptor.forClass(MusicResponse.class);
        verify(musicCatalogService).buildTrackMetadata(musicResponseCaptor2.capture());
        MusicResponse responseFromBuildTrackMetadata = musicResponseCaptor2.getValue();
        assertNotNull(responseFromBuildTrackMetadata.mp3Key());
        assertNotNull(responseFromBuildTrackMetadata.albumImgKey());

        verify(metadataFileWriter).appendMetadataInFile(trackMetadata);
    }

    @Test
    void uploadData_ShouldNotPassFilter_WhenAudioDownloadAllowedIsFalse(){
        String genreName = "ROCK";
        Genre genre = new Genre();
        MusicResponse musicResponse = MusicDataFactory.musicResponseWithAudioDownloadAllowedIsFalse();

        when(musicCatalogService.findGenreByName(genreName)).thenReturn(genre);
        when(jamendoClient.tracksPack(genreName)).thenReturn(List.of(musicResponse));

        musicImportService.uploadData(genreName);

        verifyNoMoreInteractions(musicCatalogService);
        verifyNoInteractions(metadataFileWriter);
    }

    @Test
    void uploadData_ShouldNotPassFilter_WhenDurationIsZero(){
        String genreName = "ROCK";
        Genre genre = new Genre();
        MusicResponse musicResponse = MusicDataFactory.musicResponseWithDurationIsZero();

        when(musicCatalogService.findGenreByName(genreName)).thenReturn(genre);
        when(jamendoClient.tracksPack(genreName)).thenReturn(List.of(musicResponse));

        musicImportService.uploadData(genreName);

        verifyNoMoreInteractions(musicCatalogService);
        verifyNoInteractions(metadataFileWriter);
    }
}
