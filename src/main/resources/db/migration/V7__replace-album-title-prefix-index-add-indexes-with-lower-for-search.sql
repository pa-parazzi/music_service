DROP INDEX idx_album_title_prefix;
CREATE INDEX idx_artist_name_lower_prefix ON artist(LOWER(name) text_pattern_ops);
CREATE INDEX idx_album_title_lower_prefix ON album(LOWER(title) text_pattern_ops);
CREATE INDEX idx_sound_title_lower_prefix ON sound(LOWER(title) text_pattern_ops);