package ge.edu.freeuni.petcarebackend.repository.generic.search;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class GenericSpecification<T> implements Specification<T> {

    private final List<SearchCriteria<T>> list;

    public GenericSpecification() {
        this.list = new ArrayList<>();
    }

    public GenericSpecification<T> add(String fieldName, Object value, SearchOperation operation) {
        list.add(new SearchCriteria<>(fieldName, value, operation));
        return this;
    }

    public GenericSpecification<T> add(CustomCriteria<T> customCriteria) {
        list.add(new SearchCriteria<>(customCriteria));
        return this;
    }

    @Nullable
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<>();

        for (SearchCriteria<T> criteria : list.stream().filter(c -> c.getValue() != null || c.getCustomCriteria() != null).toList()) {
            switch (criteria.getOperation()) {
                case GREATER_THAN ->
                        predicates.add(builder.greaterThan(root.get(criteria.getKey()), criteria.getValue().toString()));
                case LESS_THAN ->
                        predicates.add(builder.lessThan(root.get(criteria.getKey()), criteria.getValue().toString()));
                case GREATER_THAN_EQUAL ->
                        predicates.add(builder.greaterThanOrEqualTo(root.get(criteria.getKey()), criteria.getValue().toString()));
                case LESS_THAN_EQUAL ->
                        predicates.add(builder.lessThanOrEqualTo(root.get(criteria.getKey()), criteria.getValue().toString()));
                case NOT_EQUAL -> predicates.add(builder.notEqual(root.get(criteria.getKey()), criteria.getValue()));
                case EQUAL -> predicates.add(builder.equal(root.get(criteria.getKey()), criteria.getValue()));
                case LIKE ->
                        predicates.add(builder.like(builder.lower(root.get(criteria.getKey())), "%" + criteria.getValue().toString().toLowerCase() + "%"));
                case IN -> predicates.add(root.get(criteria.getKey()).in(criteria.getValue()));
                case NOT_IN -> predicates.add(root.get(criteria.getKey()).in(criteria.getValue()).not());
                case CUSTOM_CRITERIA -> predicates.add(criteria.getCustomCriteria().apply(root, query, builder));
            }
        }

        return builder.and(predicates.toArray(new Predicate[0]));
    }
}
