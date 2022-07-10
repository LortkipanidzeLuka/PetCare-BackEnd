package ge.edu.freeuni.petcarebackend.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import ge.edu.freeuni.petcarebackend.controller.dto.DonationDTO;
import ge.edu.freeuni.petcarebackend.controller.dto.SearchResultDTO;
import ge.edu.freeuni.petcarebackend.repository.entity.City;
import ge.edu.freeuni.petcarebackend.repository.entity.DonationAdvertisementType;
import ge.edu.freeuni.petcarebackend.repository.entity.DonationEntity;
import ge.edu.freeuni.petcarebackend.repository.entity.QDonationEntity;
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
public class DonationSearchRepositoryBean implements DonationSearchRepository {

    private final QDonationEntity qDonationEntity = QDonationEntity.donationEntity;

    private final JPAQueryFactory qf;

    public DonationSearchRepositoryBean(JPAQueryFactory qf) {
        this.qf = qf;
    }

    @Override
    public SearchResultDTO<DonationDTO> search(int page, int size, String search,
                                               DonationAdvertisementType donationAdvertisementType, City city,
                                               BigDecimal longitude, BigDecimal latitude) {
        BooleanExpression where = where(enumEq(qDonationEntity.donationAdvertisementType, donationAdvertisementType),
                boolEq(qDonationEntity.expired, false),
                enumEq(qDonationEntity.city, city),
                or(stringLike(qDonationEntity.header, search),
                        stringLike(qDonationEntity.description, search)));

        long offset = (long) size * (page - 1);

        List<DonationEntity> donationEntityList = qf.select(qDonationEntity)
                .from(qDonationEntity)
                .where(where)
                .limit(size)
                .offset(offset)
                .orderBy(getOrderByLocation(longitude, latitude).asc(), qDonationEntity.createDate.desc())
                .fetch();

        List<Long> pageSize = qf.select(qDonationEntity.count())
                .from(qDonationEntity)
                .where(where)
                .fetch();

        return new SearchResultDTO<>(donationEntityList.stream()
                .map(ad -> new DonationDTO(ad, true))
                .collect(Collectors.toList()),
                pageSize.get(0));
    }

    private NumberExpression<Double> getOrderByLocation(BigDecimal longitude, BigDecimal latitude) {
        if (longitude == null || latitude == null) {
            return Expressions.asNumber(0.0);
        }
        NumberPath<BigDecimal> lat = qDonationEntity.latitude;
        NumberPath<BigDecimal> lng = qDonationEntity.longitude;

        return (acos(cos(radians(Expressions.constant(latitude)))
                .multiply(cos(radians(lat)))
                .multiply(cos(radians(lng).subtract(radians(Expressions.constant(longitude)))))
                .add(sin(radians(Expressions.constant(latitude)))
                        .multiply(sin(radians(lat)))))
                .multiply(Expressions.constant(6371)));
    }
}
