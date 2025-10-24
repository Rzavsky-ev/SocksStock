--liquibase formatted sql

--changeset author:Eduard.Rz:1
CREATE TABLE socks
(
    id          BIGSERIAL PRIMARY KEY,
    color       VARCHAR(50) NOT NULL,
    cotton_part INTEGER NOT NULL CHECK (cotton_part >= 0 AND cotton_part <= 100),
    quantity    INTEGER NOT NULL CHECK (quantity >= 0)
);

--rollback DROP TABLE socks;