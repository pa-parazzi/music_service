package org.musicservice.demo.security.exception.errorResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenErrorResponse {

    public RefreshTokenErrorResponse(String message, Long timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    private String message;
    private Long timestamp;


}
