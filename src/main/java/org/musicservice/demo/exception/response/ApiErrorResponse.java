package org.musicservice.demo.exception.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse
        (String code, String message, int status, long timestamp, Map<String, List<String>> fieldsError) {
}
