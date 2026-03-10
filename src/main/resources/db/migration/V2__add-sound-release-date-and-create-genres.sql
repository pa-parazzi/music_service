ALTER TABLE sound ADD COLUMN release_date date;

CREATE TABLE genre(
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name varchar(50) NOT NULL UNIQUE
);

ALTER TABLE sound ADD COLUMN genre_id bigint REFERENCES genre(id) ON DELETE RESTRICT;
CREATE INDEX idx_sound_genre_id ON sound(genre_id);

INSERT INTO genre (name)
values ('ROCK'),
       ('POP'),
       ('ELECTRONIC'),
       ('JAZZ'),
       ('HIPHOP'),
       ('METAL'),
       ('CLASSICAL'),
       ('CHILLOUT'),
       ('DANCE'),
       ('TECHNO'),
       ('DUBSTEP'),
       ('FUNK'),
       ('BLUES'),
       ('INDIE'),
       ('PUNK');
