CREATE TABLE feature
(
    id SERIAL primary key,
    feature_key VARCHAR(255) UNIQUE,
    name        VARCHAR(255),
    description VARCHAR(512),
    template    BLOB,
    enabled     BOOLEAN,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP
);