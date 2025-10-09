package org.musicservice.demo.exception.errorResponse;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatusCode;

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
