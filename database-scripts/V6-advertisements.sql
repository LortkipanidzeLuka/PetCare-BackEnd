create table donation_advertisement
(
    id        bigint not null,
    donation_advertisement_type   varchar(32),
    color     varchar(32),
    age_from  smallint,
    age_until smallint,
    applicable_sex varchar(32)
);

alter table donation_advertisement
    add constraint fk_donation_id_advertisement_id foreign key (id) references advertisement (id);

grant select, insert, update, delete on donation_advertisement to pcapp;

create table pet_service_advertisement
(
    id        bigint not null,
    pet_service_type   varchar(32),
    age_from  smallint,
    age_until smallint,
    applicable_sex       varchar(32)
);

alter table pet_service_advertisement
    add constraint fk_pet_service_id_advertisement_id foreign key (id) references advertisement (id);

grant select, insert, update, delete on pet_service_advertisement to pcapp;

create table adoption_advertisement
(
    id        bigint not null,
    pet_type   varchar(32),
    color     varchar(32),
    age_from  smallint,
    age_until smallint,
    sex       varchar(32),
    breed     varchar(32)
);

alter table adoption_advertisement
    add constraint fk_pet_service_id_advertisement_id foreign key (id) references advertisement (id);

grant select, insert, update, delete on adoption_advertisement to pcapp;

create table pet_types
(
    value            varchar(32) not null,
    advertisement_id bigint      not null
);

alter table pet_types
    add constraint fk_pet_types_advertisement_id foreign key (advertisement_id) references advertisement (id),
    add constraint uk_pet_types_value_advertisement_id unique (value, advertisement_id);

grant select, insert, update, delete on pet_types to pcapp;