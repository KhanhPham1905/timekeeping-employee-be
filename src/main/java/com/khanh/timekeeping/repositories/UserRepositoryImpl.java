package com.khanh.timekeeping.repositories;

import com.khanh.timekeeping.constants.Gender;
import com.khanh.timekeeping.entities.User;
import com.khanh.timekeeping.entities.enums.UserStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;

@Transactional
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public List<User> searchTopUser(
            Gender gender, UserStatus status, LocalDateTime createdAt, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);

        Root<User> user = cq.from(User.class);
        List<Predicate> predicates = new ArrayList<>();

        if (Objects.nonNull(gender)) {
            predicates.add(cb.equal(user.get("gender"), gender));
        }

        if (Objects.nonNull(status)) {
            predicates.add(cb.equal(user.get("status"), status));
        }

        if (Objects.nonNull(createdAt)) {
            predicates.add(cb.lessThan(user.get("createdAt"), createdAt));
        }

        if (Objects.nonNull(pageable)) {
            cq.where(predicates.toArray(new Predicate[0]))
                    .orderBy(QueryUtils.toOrders(pageable.getSort(), user, cb));
            return entityManager
                    .createQuery(cq)
                    .setMaxResults(pageable.getPageSize())
                    .setFirstResult((int) pageable.getOffset())
                    .getResultList();
        } else {
            cq.where(predicates.toArray(new Predicate[0]));
            return entityManager.createQuery(cq).getResultList();
        }
    }

    @Override
    public List<User> searchTopUserWithNativeQuery(
            Gender gender, UserStatus status, LocalDateTime createdAt, Pageable pageable) {
        Map<String, Object> params = new HashMap<>();
        StringBuilder nativeQuery =
                new StringBuilder("""
        SELECT u.* FROM users u WHERE 1 = 1
    """);
        if (Objects.nonNull(gender)) {
            nativeQuery.append(" AND u.gender = :gender ");
            params.put("gender", gender);
        }
        if (Objects.nonNull(status)) {
            nativeQuery.append(" AND u.status = :status ");
            params.put("status", status);
        }
        if (Objects.nonNull(createdAt)) {
            nativeQuery.append(" AND u.created_at < :createdAt ");
            params.put("createdAt", createdAt);
        }
        if (Objects.nonNull(pageable)) {
            if (Objects.nonNull(pageable.getSort())) {
                List<String> orders =
                        pageable
                                .getSort()
                                .get()
                                .map(order -> order.getProperty() + " " + order.getDirection())
                                .toList();
                nativeQuery.append(" ORDER BY ").append(String.join(", ", orders));
            }
            nativeQuery.append(" LIMIT :limit OFFSET :offset ");
            params.put("limit", pageable.getPageSize());
            params.put("offset", pageable.getOffset());
        }
        Query query = entityManager.createNativeQuery(nativeQuery.toString(), User.class);
        if (!CollectionUtils.isEmpty(params)) {
            params.forEach(query::setParameter);
        }
        return query.getResultList();
    }
}
