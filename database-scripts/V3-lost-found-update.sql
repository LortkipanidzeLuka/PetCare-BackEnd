alter table lost_found_advertisement
    add type varchar(32) not null;

alter table app_user
    drop column profile_image;

create table advertisement_images
(
    id               bigint primary key not null,
    title            varchar(64)        not null,
    is_primary       boolean            not null,
    content          varchar            not null,
    advertisement_id bigint             not null
);
alter table advertisement_images
    add constraint fk_advertisement_images_advertisement_id foreign key (advertisement_id) references advertisement (id);
grant select, insert, update, delete on advertisement_images to pcapp;
create sequence seq_advertisement_images start with 1;
grant update on seq_advertisement_images to pcapp;

