create table email_change_otp
(
    id    bigint      not null,
    email varchar(64) not null
);
alter table email_change_otp
    add constraint fk_email_change_otp foreign key (id) references otp (id);

grant select , insert , update , delete on email_change_otp to pcapp;