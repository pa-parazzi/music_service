ALTER TABLE artist ADD COLUMN genre_id bigint REFERENCES genre(id) ON DELETE RESTRICT;
CREATE INDEX artist_genre_id_idx ON artist(genre_id);

ALTER TABLE album ADD COLUMN genre_id bigint REFERENCES genre(id) ON DELETE RESTRICT;
CREATE INDEX album_genre_id_idx ON album(genre_id);