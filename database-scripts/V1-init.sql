/**
before running this script there should be one user which is used for connection pool
this user should be granted connect permission
create role pcapp login password '{anyPasswordYouDesire}';
all objects are created in the public schema of the database
for security reasons we revoke public schema privileges from public role and grant them individually
*/
REVOKE ALL ON schema public FROM public;
grant usage on schema public to pcapp;

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
grant update on seq_user to pcapp;
