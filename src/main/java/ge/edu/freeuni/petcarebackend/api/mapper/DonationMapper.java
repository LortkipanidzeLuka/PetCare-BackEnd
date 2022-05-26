package ge.edu.freeuni.petcarebackend.api.mapper;

import ge.edu.freeuni.petcarebackend.api.dtos.DonationDto;
import ge.edu.freeuni.petcarebackend.repository.entity.DonationEntity;
import org.mapstruct.Mapper;

@Mapper
public interface DonationMapper {

    DonationDto donationDto(DonationEntity donationEntity);

    DonationEntity donationEntity(DonationDto donationDto);
}
