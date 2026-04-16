package org.musicservice.demo.exception.handler;

import org.musicservice.demo.error.ApiErrorResponse;
import org.musicservice.demo.error.ErrorType;
import org.musicservice.demo.exception.importer.WriteTrackMetadataException;
import org.musicservice.demo.exception.music.GenreDoesNotExistException;
import org.musicservice.demo.exception.music.MusicNotFoundException;
import org.musicservice.demo.exception.music.NoSuchMusicException;
import org.musicservice.demo.exception.user.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiGlobalHandler {

    @ExceptionHandler(MusicNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> apiErrorHandle(MusicNotFoundException e){
        HttpStatus status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(
                new ApiErrorResponse(ErrorType.API_ERROR.name(), e.getMessage(),
                        status.value(), System.currentTimeMillis(), null));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> userNotFoundHandle(UserNotFoundException e){
        HttpStatus status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(
                new ApiErrorResponse(ErrorType.USER_NOT_FOUND_ERROR.name(), e.getMessage(),
                        status.value(), System.currentTimeMillis(), null));
    }

    @ExceptionHandler(NoSuchMusicException.class)
    public ResponseEntity<ApiErrorResponse> noSuchMusicResultHandle(NoSuchMusicException e){
        HttpStatus status = HttpStatus.NO_CONTENT;
        return ResponseEntity.status(status).body(
                new ApiErrorResponse(ErrorType.MISSING_MUSIC_CONTENT.name(), e.getMessage(),
                        status.value(), System.currentTimeMillis(), null));
    }

    @ExceptionHandler(GenreDoesNotExistException.class)
    public ResponseEntity<ApiErrorResponse> genreDoesNotExistHandle(GenreDoesNotExistException e){
        HttpStatus status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(
                new ApiErrorResponse(ErrorType.MUSIC_GENRE_DOES_NOT_EXIST.name(), e.getMessage(),
                        status.value(), System.currentTimeMillis(), null));
    }

    @ExceptionHandler(WriteTrackMetadataException.class)
    public ResponseEntity<ApiErrorResponse> writeTrackMetadataHandler(WriteTrackMetadataException e){
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status).body(
                new ApiErrorResponse("WRITE_FILE_ERROR", e.getMessage(),
                        status.value(), System.currentTimeMillis(), null));
    }
}
