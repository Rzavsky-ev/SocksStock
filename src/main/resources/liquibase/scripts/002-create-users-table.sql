--liquibase formatted sql

--changeset author:Eduard.Rz:1
CREATE TABLE users
(
    id       BIGSERIAL PRIMARY KEY,
    username VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role     VARCHAR(20)  NOT NULL CHECK (role IN ('ROLE_USER', 'ROLE_ADMIN', 'ROLE_MODERATOR'))
);

--rollback DROP TABLE users;