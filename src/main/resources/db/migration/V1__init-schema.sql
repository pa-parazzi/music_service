-- User --
CREATE TABLE users(
    id       bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username varchar(55)  NOT NULL UNIQUE,
    email    varchar(255) NOT NULL UNIQUE,
    password varchar(255) NOT NULL,
    date_of_birth    date NOT NULL CHECK ( date_of_birth > date '1900-01-01' ),
    failed_login_attempts int NOT NULL CHECK ( failed_login_attempts >= 0 ) default 0,
    lock_time timestamptz DEFAULT NULL,
    enabled   boolean NOT NULL default false,
    role_name varchar(55)  NOT NULL
);

-- Image --
CREATE TABLE user_avatar(
    id      bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    s3_key  varchar(512)  NOT NULL default 'default_avatar.jpg',
    user_id bigint UNIQUE NOT NULL REFERENCES users (id) ON DELETE CASCADE
);


-- Authentication --
CREATE TABLE refresh_token(
    id          bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id     bigint       NOT NULL UNIQUE,
    token_hash  varchar(255) NOT NULL UNIQUE,
    expiry_date timestamptz  NOT NULL,
    revoked     boolean      NOT NULL default false
);

CREATE TABLE verification_Token(
    id          bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id     bigint          NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    token       varchar(255) NOT NULL UNIQUE,
    expiry_date timestamptz  NOT NULL
);


-- Music --
CREATE TABLE artist(
    id   bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name varchar(255) NOT NULL UNIQUE
);

CREATE TABLE album(
    id        bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title     varchar(255) NOT NULL,
    artist_id bigint NOT NULL REFERENCES Artist (id) ON DELETE RESTRICT
);

-- Image --
CREATE TABLE album_image(
    id       bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    s3_key   varchar(512)  NOT NULL,
    album_id bigint UNIQUE NOT NULL REFERENCES album (id) ON DELETE CASCADE
);

CREATE TABLE sound(
    id        bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title     varchar(255) NOT NULL,
    duration  int          NOT NULL CHECK ( duration >= 0 ),
    s3_key    varchar(512) NOT NULL,
    artist_id bigint       NOT NULL REFERENCES Artist (id) ON DELETE CASCADE,
    album_id  bigint       NOT NULL REFERENCES Album (id) ON DELETE CASCADE
);


-- Likes --
CREATE TABLE album_like(
    id         bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id    bigint      NOT NULL REFERENCES Users (id) ON DELETE CASCADE,
    album_id   bigint      NOT NULL REFERENCES Album (id) ON DELETE CASCADE,
    created_at timestamptz NOT NULL,
    CONSTRAINT unique_user_album_like UNIQUE (user_id, album_id)
);

CREATE TABLE sound_like(
    id         bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id    bigint      NOT NULL REFERENCES Users (id) ON DELETE CASCADE,
    sound_id   bigint      NOT NULL REFERENCES Sound (id) ON DELETE CASCADE,
    created_at timestamptz NOT NULL,
    CONSTRAINT unique_user_sound_like UNIQUE (user_id, sound_id)
);

-- Indexes --
create index idx_album_like_user_created on album_like (user_id, created_at desc);

create index idx_sound_like_user_created on sound_like (user_id, created_at desc);

create index idx_album_title_prefix on album (title text_pattern_ops);

create index idx_sound_artist_id on sound (artist_id);

create index idx_sound_album_id on sound (album_id);
