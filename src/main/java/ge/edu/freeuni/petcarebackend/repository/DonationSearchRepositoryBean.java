package ge.edu.freeuni.petcarebackend.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ge.edu.freeuni.petcarebackend.controller.dto.AnimalHelpDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.DonationDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.SearchResultDTO;
import ge.edu.freeuni.petcarebackend.repository.entity.*;
import ge.edu.freeuni.petcarebackend.repository.entity.QDonationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static ge.edu.freeuni.petcarebackend.repository.QueryUtils.*;
import static ge.edu.freeuni.petcarebackend.repository.QueryUtils.stringLike;

@Repository
public class DonationSearchRepositoryBean implements DonationSearchRepository {

    private final QDonationEntity qDonationEntity = QDonationEntity.donationEntity;

    @Autowired
    private JPAQueryFactory qf;

    @Override
    public SearchResultDTO<DonationDTO> search(int page, int size, boolean asc, String search,
                                                  DonationAdvertisementType donationAdvertisementType, City city,
                                                  BigDecimal longitude, BigDecimal latitude) {
        BooleanExpression where = where(enumEq(qDonationEntity.donationAdvertisementType, donationAdvertisementType),
                enumEq(qDonationEntity.city, city),
                or(stringLike(qDonationEntity.header, search),
                        stringLike(qDonationEntity.description, search)));

        List<DonationEntity> donationEntityList = qf.select(qDonationEntity)
                .where(where)
                .limit(size)
                .offset(page)
                .orderBy(asc ? getOrderByLocation(longitude, latitude).asc() : getOrderByLocation(longitude, latitude).desc() ,
                        asc? qDonationEntity.createDate.asc() : qDonationEntity.createDate.desc())
                .fetch();

        return new SearchResultDTO<>( donationEntityList.stream()
                .map(ad -> new DonationDTO(ad, true))
                .collect(Collectors.toList()),
                donationEntityList.size());
    }

    private NumberExpression<BigDecimal> getOrderByLocation(BigDecimal longitude, BigDecimal latitude) {
        if(longitude == null || latitude == null) {
            return qDonationEntity.latitude.multiply(0);
        }
        NumberExpression<BigDecimal> latitudeDifference = qDonationEntity.latitude.subtract(latitude);
        NumberExpression<BigDecimal> longitudeDifference = qDonationEntity.longitude.subtract(longitude);
        NumberExpression<BigDecimal> latitudeDifferenceSquared = latitudeDifference.multiply(latitudeDifference);
        NumberExpression<BigDecimal> longitudeDifferenceSquared = longitudeDifference.multiply(longitudeDifference);
        return longitudeDifferenceSquared.add(latitudeDifferenceSquared);
    }
}
