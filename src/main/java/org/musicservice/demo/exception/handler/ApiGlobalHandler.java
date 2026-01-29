package org.musicservice.demo.exception.handler;

import org.musicservice.demo.exception.ApiNotFoundException;
import org.musicservice.demo.exception.UserNotFoundException;
import org.musicservice.demo.exception.response.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiGlobalHandler {

    @ExceptionHandler(ApiNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> apiExceptionHandle(ApiNotFoundException e){
        HttpStatus status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(
                new ApiErrorResponse("API_ERROR", e.getMessage(),
                        status.value(), System.currentTimeMillis(), null));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> apiExceptionHandle(UserNotFoundException e){
        HttpStatus status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status).body(
                new ApiErrorResponse("USER_NOT_FOUND_ERROR", e.getMessage(),
                        status.value(), System.currentTimeMillis(), null));
    }


}
