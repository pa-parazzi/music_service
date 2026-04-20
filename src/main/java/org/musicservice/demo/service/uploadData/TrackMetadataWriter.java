package org.musicservice.demo.service.uploadData;

import org.musicservice.demo.dto.metadata.TrackMetadata;

public interface TrackMetadataWriter {
    void write(TrackMetadata metadata);
}
