package org.musicservice.demo.error;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL) // Поля со значением null не будут включены в json сериализацию
public record ApiErrorResponse
        (String code, String message, int status, long timestamp, Map<String, List<String>> fieldsError) {
}
