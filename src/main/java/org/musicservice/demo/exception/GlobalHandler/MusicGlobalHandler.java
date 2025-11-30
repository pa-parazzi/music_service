package org.musicservice.demo.exception.GlobalHandler;

import org.musicservice.demo.exception.errorResponse.ErrorResponse;
import org.musicservice.demo.exception.music.AlbumNotFoundException;
import org.musicservice.demo.exception.music.ArtistNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MusicGlobalHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> exceptionHandler(AlbumNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> exceptionHandler(ArtistNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
    }
}
