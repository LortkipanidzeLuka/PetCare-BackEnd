package ge.edu.freeuni.petcarebackend.repository;

import ge.edu.freeuni.petcarebackend.controller.dto.AdvertisementDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.SearchResultDTO;
import ge.edu.freeuni.petcarebackend.exception.BusinessException;
import ge.edu.freeuni.petcarebackend.repository.entity.City;
import ge.edu.freeuni.petcarebackend.repository.entity.Color;
import ge.edu.freeuni.petcarebackend.repository.entity.DonationAdvertisementType;
import ge.edu.freeuni.petcarebackend.repository.entity.DonationEntity;
import ge.edu.freeuni.petcarebackend.repository.generic.search.GenericSpecification;
import ge.edu.freeuni.petcarebackend.repository.generic.search.SearchOperation;
import ge.edu.freeuni.petcarebackend.security.repository.entity.Sex;
import ge.edu.freeuni.petcarebackend.security.repository.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public interface DonationRepository extends JpaRepository<DonationEntity, Long>, JpaSpecificationExecutor<DonationEntity> {
    Map<String, String> LOST_FOUND_ORDER_BY_MAP = new HashMap<>();

    Optional<DonationEntity> findByCreatorUserAndId(UserEntity creatorUser, Long id);

    void deleteByCreatorUserAndId(UserEntity creator, Long id);

    default SearchResultDTO<AdvertisementDTO> search(int page, int size, String orderBy, boolean asc, String search,
                                                     DonationAdvertisementType donationAdvertisementType, City city) {
        GenericSpecification<DonationEntity> specification = new GenericSpecification<DonationEntity>()
                .add("donationAdvertisementType", donationAdvertisementType, SearchOperation.EQUAL)
                .add("city", city, SearchOperation.EQUAL)
                .add((root, query, builder) -> Stream.of("header", "description")
                        .map(key -> builder.like(builder.lower(root.get(key)), "%" + search.toLowerCase() + "%"))
                        .reduce(builder::or).get());

        if (page == 0 || size == 0) {
            page = 1;
            size = Integer.MAX_VALUE;
        }

        String orderByString = "createDate";
        if (orderBy != null) {
            orderByString = this.LOST_FOUND_ORDER_BY_MAP.get(orderBy);
            if (orderByString == null) {
                throw new BusinessException();
            }
        }

        Pageable pageAndOrder = PageRequest.of(page - 1, size, Sort.by(asc ? Sort.Direction.ASC : Sort.Direction.DESC, orderByString));

        Page<DonationEntity> result = this.findAll(specification, pageAndOrder);

        return new SearchResultDTO<>(
                result.toList().stream().map(ad -> new AdvertisementDTO(ad, true)).collect(Collectors.toList()),
                result.getTotalElements()
        );

    }

}
