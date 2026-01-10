package org.musicservice.demo.security.dto;

import java.util.Collection;

public record TokenSubject(Long userId, Collection<String> roles) {
}
