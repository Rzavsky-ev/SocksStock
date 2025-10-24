--liquibase formatted sql

--changeset author:Eduard.Rz:1
CREATE INDEX idx_socks_color_cotton ON socks(color, cotton_part);

--changeset author:Eduard.Rz:2
CREATE INDEX idx_users_username ON users(username);

--changeset author:Eduard.Rz:3
CREATE INDEX idx_users_role ON users(role);