package ge.edu.freeuni.petcarebackend.repository;

import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ge.edu.freeuni.petcarebackend.controller.dto.AdvertisementDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.AnimalHelpDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.SearchResultDTO;
import ge.edu.freeuni.petcarebackend.repository.entity.*;
import ge.edu.freeuni.petcarebackend.repository.entity.QAnimalHelpEntity;
import ge.edu.freeuni.petcarebackend.security.repository.entity.Sex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static ge.edu.freeuni.petcarebackend.repository.QueryUtils.*;
import static ge.edu.freeuni.petcarebackend.repository.QueryUtils.stringLike;

@Repository
public class AnimalHelpSearchRepositoryBean implements AnimalHelpSearchRepository{

    private final QAnimalHelpEntity qAnimalHelpEntity = QAnimalHelpEntity.animalHelpEntity;

    @Autowired
    private JPAQueryFactory qf;

    @Override
    public SearchResultDTO<AnimalHelpDTO> search(int page, int size, boolean asc, String search,
                                                 AnimalHelpType type, PetType petType, Color color, Sex sex,
                                                 Integer ageFrom, Integer ageUntil, String breed, City city,
                                                 BigDecimal longitude, BigDecimal latitude) {
        BooleanExpression where = where(enumEq(qAnimalHelpEntity.type, type),
                enumEq(qAnimalHelpEntity.petType, petType),
                enumEq(qAnimalHelpEntity.color, color),
                enumEq(qAnimalHelpEntity.sex, sex),
                shortMoreOrEq(qAnimalHelpEntity.ageFrom, ageFrom.shortValue()),
                shortLessOrEq(qAnimalHelpEntity.ageUntil, ageUntil.shortValue()),
                stringLike(qAnimalHelpEntity.breed, breed),
                or(stringLike(qAnimalHelpEntity.header, search),
                        stringLike(qAnimalHelpEntity.description, search)));

        List<AnimalHelpEntity> animalHelpEntityList = qf.select(qAnimalHelpEntity)
                .where(where)
                .limit(size)
                .offset(page)
                .orderBy(asc ? getOrderByLocation(longitude, latitude).asc() : getOrderByLocation(longitude, latitude).desc() ,
                      asc? qAnimalHelpEntity.createDate.asc() : qAnimalHelpEntity.createDate.desc())
                .fetch();

        return new SearchResultDTO<>( animalHelpEntityList.stream()
                .map(ad -> new AnimalHelpDTO(ad, true))
                .collect(Collectors.toList()),
                animalHelpEntityList.size());
    }

    private NumberExpression<BigDecimal> getOrderByLocation(BigDecimal longitude, BigDecimal latitude) {
        if(longitude == null || latitude == null) {
           return qAnimalHelpEntity.latitude.multiply(0);
        }
        NumberExpression<BigDecimal> latitudeDifference = qAnimalHelpEntity.latitude.subtract(latitude);
        NumberExpression<BigDecimal> longitudeDifference = qAnimalHelpEntity.longitude.subtract(longitude);
        NumberExpression<BigDecimal> latitudeDifferenceSquared = latitudeDifference.multiply(latitudeDifference);
        NumberExpression<BigDecimal> longitudeDifferenceSquared = longitudeDifference.multiply(longitudeDifference);
        return longitudeDifferenceSquared.add(latitudeDifferenceSquared);
    }
}
