CREATE TABLE if not exists feature
(
    id          SERIAL primary key,
    feature_key VARCHAR(255) UNIQUE,
    name        VARCHAR(255),
    description VARCHAR(512),
    template    CLOB,
    enabled     BOOLEAN,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP
);

CREATE TABLE if not exists feature_config
(
    id                SERIAL primary key,
    feature_key       VARCHAR(255),
    config_key        VARCHAR(255),
    title             VARCHAR(255),
    description       VARCHAR(512),
    data_adapter      CLOB,
    data_adapter_type VARCHAR(255),
    sa_data           CLOB,
    static_status     VARCHAR(255),
    start_time        TIMESTAMP,
    end_time          TIMESTAMP,
    filter_condition  CLOB,
    priority          INT,
    created_at        TIMESTAMP,
    updated_at        TIMESTAMP
);