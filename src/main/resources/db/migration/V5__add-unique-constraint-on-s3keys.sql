DROP INDEX s3_album_image_key_idx;
ALTER TABLE album_image ADD CONSTRAINT album_image_s3key_unique UNIQUE (s3_key);

DROP INDEX s3_sound_key_idx;
ALTER TABLE sound ADD CONSTRAINT sound_s3key_unique UNIQUE (s3_key);