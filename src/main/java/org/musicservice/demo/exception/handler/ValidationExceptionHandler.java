package org.musicservice.demo.exception.handler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.musicservice.demo.error.ApiErrorResponse;
import org.musicservice.demo.error.ErrorType;
import org.musicservice.demo.exception.user.RegistrationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ValidationExceptionHandler {

    // Ошибки валидации DTO
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException e){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String, List<String>> errors = new HashMap<>();
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> errorList = bindingResult.getFieldErrors();
        for(FieldError error: errorList){
            errors.computeIfAbsent(error.getField(), key-> new ArrayList<>()).add(error.getDefaultMessage());
        }
        return ResponseEntity.status(status).body(
                new ApiErrorResponse(ErrorType.VALIDATION_ERROR.name(), "Validation failed",
                        status.value(), System.currentTimeMillis(), errors));
    }

    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<ApiErrorResponse> handleRegException(RegistrationException e){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String, List<String>> fieldsError = new HashMap<>();
        fieldsError.put(e.getCode().getField(), List.of(e.getCode().getErrorCode()));
        return ResponseEntity.status(status).body(
                new ApiErrorResponse(ErrorType.REGISTRATION_ERROR.name(), e.getMessage(), status.value(), System.currentTimeMillis(), fieldsError));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException e){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Throwable cause = e;
        while(cause!=null){
            if(cause instanceof DateTimeParseException){
                return ResponseEntity.status(status).body(
                        new ApiErrorResponse(ErrorType.INVALID_DATE_FORMAT.name(), "формат даты: dd/MM/yyyy",
                                status.value(), System.currentTimeMillis(), Map.of("dateOfBirth", List.of("Invalid LocalDate format")))
                );
            }
            cause = cause.getCause();
        }

        return ResponseEntity.status(status).body(
                new ApiErrorResponse(ErrorType.INVALID_BODY_REQUEST.name(), "Не верное тело запроса",
                        status.value(), System.currentTimeMillis(), Map.of("json serialization", List.of("Invalid request body")))
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException e){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String, List<String>> error = e.getConstraintViolations().stream()
                .collect(Collectors.groupingBy(
                        violation -> violation.getPropertyPath().toString(),
                        Collectors.mapping(ConstraintViolation::getMessage, Collectors.toList())
                ));
        return ResponseEntity.status(status).body(new ApiErrorResponse(ErrorType.VALIDATION_ERROR.name(),
                "Ошибка ограничения параметра", status.value(), System.currentTimeMillis(), error));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String, List<String>> error = Map.of(e.getName(), List.of(e.getMessage()));
        return ResponseEntity.status(status).body(new ApiErrorResponse(ErrorType.VALIDATION_ERROR.name(),
                "Не верный тип параметра", status.value(), System.currentTimeMillis(), error));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingServletRequestParameter(MissingServletRequestParameterException e){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Map<String, List<String>> error = Map.of(e.getParameterName(), List.of(e.getMessage()));
        return ResponseEntity.status(status).body(new ApiErrorResponse(ErrorType.VALIDATION_ERROR.name(),
                "Отсутсвует параметр запроса", status.value(), System.currentTimeMillis(), error));
    }

}
