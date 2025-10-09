package org.musicservice.demo.exception.errorResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserFieldsErrorResponse {

    public UserFieldsErrorResponse(String message, long timestamp) {
        this.timestamp = timestamp;
        this.message = message;
    }

    private String message;
    private long timestamp;

}
