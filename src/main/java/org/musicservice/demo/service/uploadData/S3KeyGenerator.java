package org.musicservice.demo.service.uploadData;

import de.huxhorn.sulky.ulid.ULID;
import org.springframework.stereotype.Component;

@Component
public class S3KeyGenerator {

    private static final ULID ulid = new ULID();

    public String generateUploadAlbumImageKey(){
        return "albums/" + ulid.nextULID() + ".jpg";
    }

    public String generateUploadMp3Key(){
        return ulid.nextULID() + ".mp3";
    }
}
