create table donation_advertisement
(
    id        bigint not null,
    type   varchar(32),
    color     varchar(32),
    age_from  smallint,
    age_until smallint,
    sex       varchar(32),
);

alter table donation_advertisement
    add constraint fk_donation_id_advertisement_id foreign key (id) references advertisement (id);

create table pet_service_advertisement
(
    id        bigint not null,
    type   varchar(32),
    age_from  smallint,
    age_until smallint,
    applicableSex       varchar(32),
);

alter table pet_service_advertisement
    add constraint fk_pet_service_id_advertisement_id foreign key (id) references advertisement (id);