package org.musicservice.demo.security.dto;

import lombok.Data;

@Data
public class VerifyEmailRequest {
    private Long userId;
    private String email;
}
