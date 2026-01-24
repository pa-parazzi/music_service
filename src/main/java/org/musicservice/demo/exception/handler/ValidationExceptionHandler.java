package org.musicservice.demo.exception.handler;

import org.musicservice.demo.exception.RegistrationException;
import org.musicservice.demo.exception.response.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ValidationExceptionHandler {

    // Ошибки валидации DTO
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException e){
        Map<String, List<String>> errors = new HashMap<>();
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> errorList = bindingResult.getFieldErrors();
        for(FieldError error: errorList){
            errors.computeIfAbsent(error.getField(), key-> new ArrayList<>()).add(error.getDefaultMessage());
        }
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(
                new ApiErrorResponse("VALIDATION_ERROR", "Validation failed",
                        status.value(), System.currentTimeMillis(), errors));
    }

    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<ApiErrorResponse> handleRegException(RegistrationException e){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String, List<String>> fieldsError = new HashMap<>();
        fieldsError.put(e.getCode().getField(), List.of(e.getCode().getErrorCode()));
        return ResponseEntity.status(status).body(
                new ApiErrorResponse("REGISTRATION_ERROR", e.getMessage(), status.value(), System.currentTimeMillis(), fieldsError));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException e){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Throwable cause = e;
        while(cause!=null){
            if(cause instanceof DateTimeParseException){
                return ResponseEntity.status(status).body(
                        new ApiErrorResponse("INVALID_DATE_FORMAT", "формат даты: dd/MM/yyyy",
                                status.value(), System.currentTimeMillis(), Map.of("dateOfBirth", List.of("Invalid LocalDate format")))
                );
            }
            cause = cause.getCause();
        }

        return ResponseEntity.status(status).body(
                new ApiErrorResponse("INVALID_BODY_REQUEST", "Не верное тело запроса",
                        status.value(), System.currentTimeMillis(), Map.of("json serialization", List.of("Invalid request body")))
        );
    }


}
