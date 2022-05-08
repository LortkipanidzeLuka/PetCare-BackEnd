create table advertisement
(
    id           bigint       not null primary key,
    header       varchar(128) not null,
    creator_user bigint       not null,
    create_date  date         not null,
    longitude    decimal,
    latitude     decimal,
    city         varchar(32)  not null,
    description  varchar(512)
);
alter table advertisement
    add constraint fk_advertisement_user_id foreign key (creator_user) references app_user (id);
grant select, insert, update, delete on advertisement to pcapp;

create sequence seq_advertisement start with 1;
grant update on seq_advertisement to pcapp;

create table tags
(
    value            varchar(32) not null,
    advertisement_id bigint      not null
);

alter table tags
    add constraint fk_tags_advertisement_id foreign key (advertisement_id) references advertisement (id),
    add constraint uk_tags_value_advertisement_id unique (value, advertisement_id);

grant select, insert, update, delete on tags to pcapp;

create table lost_found_advertisement
(
    id        bigint not null,
    petType   varchar(32),
    color     varchar(32),
    age_from  smallint,
    age_until smallint,
    sex       varchar(32),
    breed     varchar(32)
);

alter table lost_found_advertisement
    add constraint fk_lost_and_found_id_advertisement_id foreign key (id) references advertisement (id);

grant select, insert, update, delete on lost_found_advertisement to pcapp;