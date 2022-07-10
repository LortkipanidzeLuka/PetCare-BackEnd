drop table adoption_advertisement;

alter table donation_advertisement
drop column color;

alter table donation_advertisement
drop column age_from;

alter table donation_advertisement
drop column age_until;

alter table donation_advertisement
drop column applicable_sex;

alter table pet_service_advertisement
drop column age_from;

alter table pet_service_advertisement
drop column age_until;

alter table pet_service_advertisement
drop column applicable_sex;


ALTER TABLE lost_found_advertisement RENAME  TO animal_help_advertisement;
