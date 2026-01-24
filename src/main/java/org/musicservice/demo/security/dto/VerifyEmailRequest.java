package org.musicservice.demo.security.dto;

public record VerifyEmailRequest (Long userId, String email) {
}
