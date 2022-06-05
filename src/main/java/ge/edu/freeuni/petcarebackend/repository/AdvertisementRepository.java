package ge.edu.freeuni.petcarebackend.repository;

import ge.edu.freeuni.petcarebackend.controller.dto.AdvertisementDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.SearchResultDTO;
import ge.edu.freeuni.petcarebackend.exception.BusinessException;
import ge.edu.freeuni.petcarebackend.repository.entity.AdvertisementEntity;
import ge.edu.freeuni.petcarebackend.repository.entity.AdvertisementType;
import ge.edu.freeuni.petcarebackend.repository.generic.search.GenericSpecification;
import ge.edu.freeuni.petcarebackend.repository.generic.search.SearchOperation;
import ge.edu.freeuni.petcarebackend.security.repository.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface AdvertisementRepository extends JpaRepository<AdvertisementEntity, Long>, JpaSpecificationExecutor<AdvertisementEntity> {

    List<AdvertisementEntity> findByCreatorUser(UserEntity user);

    Map<String, String> ADVERTISEMENT_ORDER_BY_MAP = new HashMap<>(); // TODO: sortBy?


    default SearchResultDTO<AdvertisementDTO> search(
            int page, int size, String orderBy, boolean asc,
            String search, AdvertisementType type, UserEntity creatorUser
    ) {
        GenericSpecification<AdvertisementEntity> specification = new GenericSpecification<AdvertisementEntity>()
                .add((root, query, builder) -> Stream.of("header", "description")
                        .map(key -> builder.like(builder.lower(root.get(key)), "%" + search.toLowerCase() + "%"))
                        .reduce(builder::or).get())
                .add("advertisementType", type, SearchOperation.EQUAL)
                .add("creatorUser", creatorUser, SearchOperation.EQUAL);

        if (page == 0 || size == 0) {
            page = 1;
            size = Integer.MAX_VALUE;
        }

        String orderByString = "createDate";
        if (orderBy != null) {
            orderByString = this.ADVERTISEMENT_ORDER_BY_MAP.get(orderBy);
            if (orderByString == null) {
                throw new BusinessException();
            }
        }

        Pageable pageAndOrder = PageRequest.of(page - 1, size, Sort.by(asc ? Sort.Direction.ASC : Sort.Direction.DESC, orderByString));

        Page<AdvertisementEntity> result = this.findAll(specification, pageAndOrder);

        return new SearchResultDTO<>(
                result.toList().stream().map(ad -> new AdvertisementDTO(ad, true)).collect(Collectors.toList()),
                result.getTotalElements()
        );
    }


}
