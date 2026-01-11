CREATE TABLE if not exists "feature"
(
    "ID"          SERIAL primary key,
    "FEATURE_KEY" VARCHAR(255) UNIQUE,
    "NAME"        VARCHAR(255),
    "DESCRIPTION" VARCHAR(512),
    "TEMPLATE"    CLOB,
    "ENABLED"     BOOLEAN,
    "CREATED_AT"  TIMESTAMP,
    "UPDATED_AT"  TIMESTAMP
);

CREATE TABLE if not exists "feature_config"
(
    "ID"                SERIAL primary key,
    "FEATURE_KEY"       VARCHAR(255),
    "CONFIG_KEY"        VARCHAR(255),
    "TITLE"             VARCHAR(255),
    "DESCRIPTION"       VARCHAR(512),
    "DATA_ADAPTER"      CLOB,
    "DATA_ADAPTER_TYPE" VARCHAR(255),
    "SA_DATA"           CLOB,
    "STATIC_STATUS"     VARCHAR(255),
    "START_TIME"        TIMESTAMP,
    "END_TIME"          TIMESTAMP,
    "FILTER_CONDITION"  CLOB,
    "PRIORITY"          INT,
    "CREATED_AT"        TIMESTAMP,
    "UPDATED_AT"        TIMESTAMP
);
