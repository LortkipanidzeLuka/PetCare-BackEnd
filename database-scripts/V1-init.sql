create table app_user
(
    id            bigint      not null primary key,
    username      varchar(64) not null unique,
    firstname     varchar(64),
    lastname      varchar(64),
    password      varchar(64) not null,
    sex           varchar(16),
    phone_number  varchar(16),
    profile_image varchar
);

grant select , insert , update, delete on app_user to pcapp;
create sequence seq_user start 1;
