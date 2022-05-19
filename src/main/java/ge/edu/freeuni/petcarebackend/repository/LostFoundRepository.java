package ge.edu.freeuni.petcarebackend.repository;

import ge.edu.freeuni.petcarebackend.controller.dto.SearchResultDTO;
import ge.edu.freeuni.petcarebackend.exception.BusinessException;
import ge.edu.freeuni.petcarebackend.repository.entity.City;
import ge.edu.freeuni.petcarebackend.repository.entity.Color;
import ge.edu.freeuni.petcarebackend.repository.entity.LostFoundEntity;
import ge.edu.freeuni.petcarebackend.repository.entity.PetType;
import ge.edu.freeuni.petcarebackend.repository.entity.Type;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public interface LostFoundRepository extends JpaRepository<LostFoundEntity, Long>, JpaSpecificationExecutor<LostFoundEntity> {

    Optional<LostFoundEntity> findByTypeAndId(Type type, Long id);

    void deleteByCreatorUserAndTypeAndId(UserEntity creator, Type type, Long id);

    Map<String, String> LOST_FOUND_ORDER_BY_MAP = new HashMap<>(); // TODO: sortBy?


    default SearchResultDTO<LostFoundEntity> search(
            int page, int size, String orderBy, boolean asc, String search,
            Type type, PetType petType, Color color, Sex sex,
            Integer ageFrom, Integer ageUntil, String breed, City city
    ) {
        GenericSpecification<LostFoundEntity> specification = new GenericSpecification<LostFoundEntity>()
                .add("type", type, SearchOperation.EQUAL)
                .add("petType", petType, SearchOperation.EQUAL)
                .add("color", color, SearchOperation.EQUAL)
                .add("sex", sex, SearchOperation.EQUAL)
                .add("ageFrom", ageFrom, SearchOperation.GREATER_THAN_EQUAL)
                .add("ageUntil", ageUntil, SearchOperation.LESS_THAN_EQUAL)
                .add("breed", breed, SearchOperation.LIKE)
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

        Page<LostFoundEntity> result = this.findAll(specification, pageAndOrder);

        return new SearchResultDTO<>(
                result.toList(),
                result.getTotalElements()
        );
    }

    Optional<LostFoundEntity> findByCreatorUserAndId(UserEntity creatorUser, Long id);

}
