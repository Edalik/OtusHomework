create table if not exists users
(
    id       SERIAL primary key,
    login    varchar(255),
    password varchar(255),
    nickname varchar(255)
);