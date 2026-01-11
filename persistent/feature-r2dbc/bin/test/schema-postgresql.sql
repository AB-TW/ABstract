CREATE TABLE IF NOT EXISTS feature (
    id          SERIAL PRIMARY KEY,
    feature_key VARCHAR(255) UNIQUE,
    name        VARCHAR(255),
    description VARCHAR(512),
    template    TEXT,
    enabled     BOOLEAN,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP
);

CREATE TABLE IF NOT EXISTS feature_config (
    id                SERIAL PRIMARY KEY,
    feature_key       VARCHAR(255),
    config_key        VARCHAR(255),
    title             VARCHAR(255),
    description       VARCHAR(512),
    data_adapter      TEXT,
    data_adapter_type VARCHAR(255),
    sa_data           TEXT,
    static_status     VARCHAR(255),
    start_time        TIMESTAMP,
    end_time          TIMESTAMP,
    filter_condition  TEXT,
    priority          INT,
    created_at        TIMESTAMP,
    updated_at        TIMESTAMP
);
