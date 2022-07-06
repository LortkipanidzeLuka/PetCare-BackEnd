package ge.edu.freeuni.petcarebackend.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ge.edu.freeuni.petcarebackend.controller.dto.AdvertisementDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.DonationDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.PetServiceDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.SearchResultDTO;
import ge.edu.freeuni.petcarebackend.repository.entity.*;
import ge.edu.freeuni.petcarebackend.repository.entity.QDonationEntity;
import ge.edu.freeuni.petcarebackend.repository.entity.QPetServiceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static ge.edu.freeuni.petcarebackend.repository.QueryUtils.*;
import static ge.edu.freeuni.petcarebackend.repository.QueryUtils.stringLike;

@Repository
public class PetServiceSearchRepositoryBean implements PetServiceSearchRepository {

    private final QPetServiceEntity qPetServiceEntity = QPetServiceEntity.petServiceEntity;

    @Autowired
    private JPAQueryFactory qf;

    @Override
    public SearchResultDTO<PetServiceDTO> search(int page, int size, boolean asc, String search,
                                                 PetServiceType petServiceType, String breed, City city,
                                                 BigDecimal longitude, BigDecimal latitude) {
        BooleanExpression where = where(enumEq(qPetServiceEntity.petServiceType, petServiceType),
                enumEq(qPetServiceEntity.city, city),
                or(stringLike(qPetServiceEntity.header, search),
                        stringLike(qPetServiceEntity.description, search)));

        List<PetServiceEntity> petServiceEntityList = qf.select(qPetServiceEntity)
                .where(where)
                .limit(size)
                .offset(page)
                .orderBy(asc ? getOrderByLocation(longitude, latitude).asc() : getOrderByLocation(longitude, latitude).desc() ,
                        asc? qPetServiceEntity.createDate.asc() : qPetServiceEntity.createDate.desc())
                .fetch();

        return new SearchResultDTO<>( petServiceEntityList.stream()
                .map(ad -> new PetServiceDTO(ad, true))
                .collect(Collectors.toList()),
                petServiceEntityList.size());
    }

    private NumberExpression<BigDecimal> getOrderByLocation(BigDecimal longitude, BigDecimal latitude) {
        if(longitude == null || latitude == null) {
            return qPetServiceEntity.latitude.multiply(0);
        }
        NumberExpression<BigDecimal> latitudeDifference = qPetServiceEntity.latitude.subtract(latitude);
        NumberExpression<BigDecimal> longitudeDifference = qPetServiceEntity.longitude.subtract(longitude);
        NumberExpression<BigDecimal> latitudeDifferenceSquared = latitudeDifference.multiply(latitudeDifference);
        NumberExpression<BigDecimal> longitudeDifferenceSquared = longitudeDifference.multiply(longitudeDifference);
        return longitudeDifferenceSquared.add(latitudeDifferenceSquared);
    }
}

