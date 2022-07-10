package ge.edu.freeuni.petcarebackend.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ge.edu.freeuni.petcarebackend.controller.dto.AnimalHelpDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.SearchResultDTO;
import ge.edu.freeuni.petcarebackend.repository.entity.*;
import ge.edu.freeuni.petcarebackend.repository.entity.QAnimalHelpEntity;
import ge.edu.freeuni.petcarebackend.security.repository.entity.Sex;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import static com.querydsl.core.types.dsl.MathExpressions.acos;
import static com.querydsl.core.types.dsl.MathExpressions.cos;
import static com.querydsl.core.types.dsl.MathExpressions.radians;
import static com.querydsl.core.types.dsl.MathExpressions.sin;
import static ge.edu.freeuni.petcarebackend.repository.QueryUtils.*;

@Repository
public class AnimalHelpSearchRepositoryBean implements AnimalHelpSearchRepository {

    private final QAnimalHelpEntity qAnimalHelpEntity = QAnimalHelpEntity.animalHelpEntity;

    private final JPAQueryFactory qf;

    public AnimalHelpSearchRepositoryBean(JPAQueryFactory qf) {
        this.qf = qf;
    }

    @Override
    public SearchResultDTO<AnimalHelpDTO> search(int page, int size, String search,
                                                 AnimalHelpType type, PetType petType, Color color, Sex sex,
                                                 Integer ageFrom, Integer ageUntil, String breed, City city,
                                                 BigDecimal longitude, BigDecimal latitude) {
        BooleanExpression where = where(enumEq(qAnimalHelpEntity.type, type),
                enumEq(qAnimalHelpEntity.petType, petType),
                enumEq(qAnimalHelpEntity.color, color),
                enumEq(qAnimalHelpEntity.sex, sex),
                boolEq(qAnimalHelpEntity.expired, false),
                ageFrom == null ? True() : shortMoreOrEq(qAnimalHelpEntity.ageFrom, ageFrom.shortValue()),
                ageUntil == null ? True() : shortLessOrEq(qAnimalHelpEntity.ageUntil, ageUntil.shortValue()),
                stringLike(qAnimalHelpEntity.breed, breed),
                or(stringLike(qAnimalHelpEntity.header, search),
                        stringLike(qAnimalHelpEntity.description, search)));

        long offset = (long) size * (page - 1);

        List<AnimalHelpEntity> animalHelpEntityList = qf.select(qAnimalHelpEntity)
                .from(qAnimalHelpEntity)
                .where(where)
                .limit(size)
                .offset(offset)
                .orderBy(getOrderByLocation(longitude, latitude).asc(), qAnimalHelpEntity.createDate.desc())
                .fetch();

        List<Long> pageSize = qf.select(qAnimalHelpEntity.count())
                .from(qAnimalHelpEntity)
                .where(where)
                .fetch();

        return new SearchResultDTO<>(animalHelpEntityList.stream()
                .map(ad -> new AnimalHelpDTO(ad, true))
                .collect(Collectors.toList()),
                pageSize.get(0));
    }

    private NumberExpression<Double> getOrderByLocation(BigDecimal longitude, BigDecimal latitude) {
        if (longitude == null || latitude == null) {
            return Expressions.asNumber(0.0);
        }
        NumberPath<BigDecimal> lat = qAnimalHelpEntity.latitude;
        NumberPath<BigDecimal> lng = qAnimalHelpEntity.longitude;

        return (acos(cos(radians(Expressions.constant(latitude)))
                .multiply(cos(radians(lat)))
                .multiply(cos(radians(lng).subtract(radians(Expressions.constant(longitude)))))
                .add(sin(radians(Expressions.constant(latitude)))
                        .multiply(sin(radians(lat)))))
                .multiply(Expressions.constant(6371)));
    }
}
