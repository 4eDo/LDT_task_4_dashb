CREATE TABLE messages (
    id              bigserial       PRIMARY KEY,
    username        varchar(256)    NOT NULL,
    create_date     timestamp       NOT NULL,
    postamat        varchar(128),
    order_id        varchar(128),
    message         text,
    stars           integer,
    category        integer,
    partner         varchar(256)
);

CREATE TABLE postamats (
    id                  varchar(128)    PRIMARY KEY,
    post_index          text,
    address             text,
    ip_address          text,
    latitude            real,
    longitude           real,
    setup_date          timestamp,
    software_version    text,
    comment             text,
    type                text,
    location            text
);

CREATE TABLE tickets (
    id          bigserial    PRIMARY KEY,
    category    integer,
    partner     varchar(256),
    messages    bigint[],
    create_date timestamp,
    comment     text
);

CREATE TABLE categories (
    id          integer    PRIMARY KEY,
    code        varchar(64),
    description text,
    archetype   varchar(64),
    parent      integer
);

CREATE TABLE archetypes (
    id          varchar(64)    PRIMARY KEY,
    description text
);
INSERT INTO archetypes (id, description) VALUES
    ('NOT_BUG', 'Проблем нет'),
    ('PM_BUG', 'Проблема с постаматом'),
    ('SOFTWARE_BUG', 'Проблема с ПО'),
    ('PARTNER_BUG', 'Проблема со стороны партнёра');

CREATE TABLE partners (
    id          bigserial    PRIMARY KEY,
    name        varchar(256),
    contacts    text,
    delegate    text,
    description text
);