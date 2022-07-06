package ge.edu.freeuni.petcarebackend.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ge.edu.freeuni.petcarebackend.controller.dto.AdvertisementDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.SearchResultDTO;
import ge.edu.freeuni.petcarebackend.repository.entity.AdvertisementEntity;
import ge.edu.freeuni.petcarebackend.repository.entity.AdvertisementType;
import ge.edu.freeuni.petcarebackend.repository.entity.QAdvertisementEntity;
import ge.edu.freeuni.petcarebackend.security.repository.entity.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static ge.edu.freeuni.petcarebackend.repository.QueryUtils.*;

@Repository
public class AdvertisementSearchRepositoryBean implements AdvertisementSearchRepository {

    private final JPAQueryFactory qf;

    private final QAdvertisementEntity qAdvertisementEntity = QAdvertisementEntity.advertisementEntity;

    public AdvertisementSearchRepositoryBean(JPAQueryFactory qf) {
        this.qf = qf;
    }

    public SearchResultDTO<AdvertisementDTO> search(
            int page, int size, boolean asc,
            String search, AdvertisementType type, UserEntity creatorUser
    ) {

        BooleanExpression where = where(enumEq(qAdvertisementEntity.advertisementType, type),
                longEq(qAdvertisementEntity.creatorUser.id, creatorUser.getId()),
                or(stringLike(qAdvertisementEntity.header, search),
                        stringLike(qAdvertisementEntity.description, search)));

        long offset = (long) size * (page - 1);

        List<AdvertisementEntity> advertisementEntityList = qf.select(qAdvertisementEntity)
                .from(qAdvertisementEntity)
                .where(where)
                .limit(size)
                .offset(offset)
                .orderBy(asc ? qAdvertisementEntity.createDate.asc() : qAdvertisementEntity.createDate.desc())
                .fetch();

        return new SearchResultDTO<>(
                advertisementEntityList.stream().map(ad -> new AdvertisementDTO(ad, true)).collect(Collectors.toList()),
                advertisementEntityList.size()
        );
    }
}
