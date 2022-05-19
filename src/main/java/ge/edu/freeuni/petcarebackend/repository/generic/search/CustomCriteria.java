package ge.edu.freeuni.petcarebackend.repository.generic.search;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@FunctionalInterface
public interface CustomCriteria<T> {

    Predicate apply(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder);

}
