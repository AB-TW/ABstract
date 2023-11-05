CREATE TABLE experiment
(
    id               SERIAL primary key,
    name             VARCHAR(255),
    description      VARCHAR(512),
    feature_key      VARCHAR(255),
    static_status    VARCHAR(255),
    start_time       TIMESTAMP,
    end_time         TIMESTAMP,
    end_collectTime  TIMESTAMP,
    buckets          CLOB,
    created_at       TIMESTAMP,
    updated_at       TIMESTAMP,
    filter_condition CLOB,
    config_version   INTEGER
);

CREATE TABLE assignment
(
    id                        SERIAL primary key,
    experiment_id             VARCHAR(255),
    client_id                 VARCHAR(255),
    bucket_key                VARCHAR(255),
    experiment_config_version INTEGER,
    extra_info                CLOB
);
