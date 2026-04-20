package org.musicservice.demo.unit.service.uploadData;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.musicservice.demo.dto.metadata.TrackMetadata;
import org.musicservice.demo.entity.genre.Genre;
import org.musicservice.demo.entity.genre.GenreName;
import org.musicservice.demo.exception.music.GenreDoesNotExistException;
import org.musicservice.demo.integration.jamendo.JamendoClient;
import org.musicservice.demo.integration.jamendo.response.MusicResponse;
import org.musicservice.demo.service.music.GenreService;
import org.musicservice.demo.service.uploadData.MusicCatalogService;
import org.musicservice.demo.service.uploadData.MusicImportService;
import org.musicservice.demo.service.uploadData.S3KeyGenerator;
import org.musicservice.demo.service.uploadData.TrackMetadataWriter;
import org.musicservice.demo.support.factory.unit.music.MusicDataFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MusicImportServiceTest {

    @Mock
    private GenreService genreService;
    @Mock
    private MusicCatalogService musicCatalogService;
    @Mock
    private JamendoClient jamendoClient;
    @Mock
    private TrackMetadataWriter trackMetadataWriter;
    @Mock
    private S3KeyGenerator s3KeyGenerator;

    @InjectMocks
    private MusicImportService musicImportService;

    private final String genreName = GenreName.BLUES.name();
    private final Genre genre = new Genre();
    private final int sizeApiContent = 10;
    private final List<MusicResponse> apiResponseList = MusicDataFactory.musicResponseList(sizeApiContent);
    private final String mp3Key = "bad-romance.mp3";
    private final String albumImgKey = "gaga.jpg";

    @Test
    void importProcess_ShouldImportTracksAndWriteMetadata_WhenSaveMusicDataSuccessful(){
        when(genreService.findGenreByName(genreName)).thenReturn(genre);
        when(jamendoClient.tracksPack(genreName)).thenReturn(apiResponseList);
        when(s3KeyGenerator.generateUploadMp3Key()).thenReturn(mp3Key);
        when(s3KeyGenerator.generateUploadAlbumImageKey()).thenReturn(albumImgKey);
        when(musicCatalogService.saveMusicData(any(MusicResponse.class), eq(genre))).thenReturn(true);

        musicImportService.importProcess(genreName);

        verify(genreService).findGenreByName(genreName);
        verify(jamendoClient).tracksPack(genreName);
        verify(s3KeyGenerator, times(sizeApiContent)).generateUploadMp3Key();
        verify(s3KeyGenerator, times(sizeApiContent)).generateUploadAlbumImageKey();
        verify(musicCatalogService, times(sizeApiContent)).saveMusicData(any(MusicResponse.class), eq(genre));
        verify(trackMetadataWriter, times(sizeApiContent)).write(any(TrackMetadata.class));
    }

    @Test
    void importProcess_ShouldReturnEarly_WhenThrowGenreDoesNotExistException() {
        when(genreService.findGenreByName(genreName)).thenThrow(GenreDoesNotExistException.class);

        assertThrows(GenreDoesNotExistException.class, () -> musicImportService.importProcess(genreName));

        verifyNoMoreInteractions(genreService);
        verifyNoInteractions(s3KeyGenerator, musicCatalogService, trackMetadataWriter);
    }

    @Test
    void importProcess_ShouldDoNothing_WhenMusicResponseWithAudioDownloadAllowedIsFalse(){
        MusicResponse apiResponse = MusicDataFactory.musicResponseWithAudioDownloadAllowedIsFalse();
        when(genreService.findGenreByName(genreName)).thenReturn(genre);
        when(jamendoClient.tracksPack(genreName)).thenReturn(List.of(apiResponse));

        musicImportService.importProcess(genreName);

        verifyNoInteractions(s3KeyGenerator, musicCatalogService, trackMetadataWriter);
    }

    @Test
    void importProcess_ShouldDoNothing_WhenMusicResponseWithDurationIsZero(){
        MusicResponse apiResponse = MusicDataFactory.musicResponseWithDurationIsZero();
        when(genreService.findGenreByName(genreName)).thenReturn(genre);
        when(jamendoClient.tracksPack(genreName)).thenReturn(List.of(apiResponse));

        musicImportService.importProcess(genreName);

        verifyNoInteractions(s3KeyGenerator, musicCatalogService, trackMetadataWriter);
    }

    @Test
    void importProcess_ShouldDontWriteTrackMetadata_WhenMusicDataNotSaved(){
        when(genreService.findGenreByName(genreName)).thenReturn(genre);
        when(jamendoClient.tracksPack(genreName)).thenReturn(apiResponseList);
        when(s3KeyGenerator.generateUploadMp3Key()).thenReturn(mp3Key);
        when(s3KeyGenerator.generateUploadAlbumImageKey()).thenReturn(albumImgKey);
        when(musicCatalogService.saveMusicData(any(MusicResponse.class), eq(genre))).thenReturn(false);

        musicImportService.importProcess(genreName);

        verify(musicCatalogService, times(sizeApiContent)).saveMusicData(any(MusicResponse.class), eq(genre));
        verifyNoInteractions(trackMetadataWriter);
    }

}
