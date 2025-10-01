package org.musicservice.demo.exception.GlobalHandler;

import org.musicservice.demo.exception.UserFieldsErrorResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalHandler {

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> exceptionHandler(MethodArgumentNotValidException e){
        Map<String, String> errors = new HashMap<>();
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> errorList = bindingResult.getFieldErrors();
        for(FieldError error: errorList){
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(errors);
    }
}
