package org.musicservice.demo.dto.music.common;

import java.util.List;

public record PageResponse<T> (List<T> content, Boolean hasNextPage) {}