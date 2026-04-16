package org.musicservice.demo.integration.jamendo.response;

import java.util.List;

public record JamendoResponse(List<MusicResponse> results) {}