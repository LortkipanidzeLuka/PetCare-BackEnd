package ge.edu.freeuni.petcarebackend.controller.mapper;

import ge.edu.freeuni.petcarebackend.controller.dto.DonationDTO;
import ge.edu.freeuni.petcarebackend.repository.entity.DonationEntity;
import org.mapstruct.Mapper;

@Mapper
public interface DonationMapper {

    DonationDTO donationDto(DonationEntity donationEntity);

    DonationEntity donationEntity(DonationDTO donationDto);
}
