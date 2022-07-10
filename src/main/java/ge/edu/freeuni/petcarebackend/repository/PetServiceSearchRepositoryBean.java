package ge.edu.freeuni.petcarebackend.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ge.edu.freeuni.petcarebackend.controller.dto.PetServiceDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.SearchResultDTO;
import ge.edu.freeuni.petcarebackend.repository.entity.City;
import ge.edu.freeuni.petcarebackend.repository.entity.PetServiceEntity;
import ge.edu.freeuni.petcarebackend.repository.entity.PetServiceType;
import ge.edu.freeuni.petcarebackend.repository.entity.QPetServiceEntity;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static com.querydsl.core.types.dsl.MathExpressions.acos;
import static com.querydsl.core.types.dsl.MathExpressions.cos;
import static com.querydsl.core.types.dsl.MathExpressions.radians;
import static com.querydsl.core.types.dsl.MathExpressions.sin;
import static ge.edu.freeuni.petcarebackend.repository.QueryUtils.*;

@Repository
public class PetServiceSearchRepositoryBean implements PetServiceSearchRepository {

    private final QPetServiceEntity qPetServiceEntity = QPetServiceEntity.petServiceEntity;

    private final JPAQueryFactory qf;

    public PetServiceSearchRepositoryBean(JPAQueryFactory qf) {
        this.qf = qf;
    }

    @Override
    public SearchResultDTO<PetServiceDTO> search(int page, int size, String search,
                                                 PetServiceType petServiceType, String breed, City city,
                                                 BigDecimal longitude, BigDecimal latitude) {
        BooleanExpression where = where(enumEq(qPetServiceEntity.petServiceType, petServiceType),
                boolEq(qPetServiceEntity.expired, false),
                enumEq(qPetServiceEntity.city, city),
                or(stringLike(qPetServiceEntity.header, search),
                        stringLike(qPetServiceEntity.description, search)));

        long offset = (long) size * (page - 1);

        List<PetServiceEntity> petServiceEntityList = qf.select(qPetServiceEntity)
                .from(qPetServiceEntity)
                .where(where)
                .limit(size)
                .offset(offset)
                .orderBy(getOrderByLocation(longitude, latitude).asc(), qPetServiceEntity.createDate.desc())
                .fetch();

        List<Long> pageSize = qf.select(qPetServiceEntity.count())
                .from(qPetServiceEntity)
                .where(where)
                .fetch();

        return new SearchResultDTO<>(petServiceEntityList.stream()
                .map(ad -> new PetServiceDTO(ad, true))
                .collect(Collectors.toList()),
                pageSize.get(0));
    }

    private NumberExpression<Double> getOrderByLocation(BigDecimal longitude, BigDecimal latitude) {
        if (longitude == null || latitude == null) {
            return Expressions.asNumber(0.0);
        }
        NumberPath<BigDecimal> lat = qPetServiceEntity.latitude;
        NumberPath<BigDecimal> lng = qPetServiceEntity.longitude;

        return (acos(cos(radians(Expressions.constant(latitude)))
                .multiply(cos(radians(lat))
                        .multiply(cos(radians(lng).subtract(radians(Expressions.constant(longitude)))
                                .add(sin(radians(Expressions.constant(latitude)))
                                        .multiply(sin(radians(lat))))))))
                .multiply(Expressions.constant(6371)));
    }
}

