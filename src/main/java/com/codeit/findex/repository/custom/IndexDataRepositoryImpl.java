package com.codeit.findex.repository.custom;

import com.codeit.findex.entity.IndexData;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class IndexDataRepositoryImpl implements IndexDataRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Slice<IndexData> findSlice(Long indexInfoId,
                                      LocalDate startDate,
                                      LocalDate endDate,
                                      Long idAfter,
                                      int size,
                                      Sort sort) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<IndexData> cq = cb.createQuery(IndexData.class);
        Root<IndexData> root = cq.from(IndexData.class);

        List<Predicate> predicates = new ArrayList<>();

        // 만약 값들이 null이라면 아예 추가하지 않음
        if(indexInfoId != null) {
            predicates.add(cb.equal(root.get("indexInfo").get("id"), indexInfoId));
        }
        if(startDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("baseDate"), startDate));
        }
        if(endDate != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("baseDate"), endDate));
        }

        if (idAfter != null) {
            predicates.add(cb.greaterThan(root.get("id"), idAfter));
        }
        if (idAfter != null) {
            predicates.add(cb.greaterThan(root.get("id"), idAfter));
        }

        cq.where(predicates.toArray(Predicate[]::new));

        // 정렬: 기본은 baseDate desc, tie-breaker로 id asc 권장
        List<Order> orders = new ArrayList<>();
        if (sort != null && sort.isSorted()) {
            sort.forEach(o -> {
                Path<?> path = root.get(o.getProperty());
                orders.add(o.isAscending() ? cb.asc(path) : cb.desc(path));
            });
        } else {
            orders.add(cb.desc(root.get("baseDate")));
        }
        // tie-breaker
        orders.add(cb.asc(root.get("id")));
        cq.orderBy(orders);

        // 페이지네이션 (커서니까 offset=0, size+1로 hasNext 판별)
        TypedQuery<IndexData> query = em.createQuery(cq)
                .setFirstResult(0)
                .setMaxResults(size + 1);

        List<IndexData> result = query.getResultList();
        boolean hasNext = result.size() > size;
        if (hasNext) result.remove(result.size() - 1);

        Pageable pageable = PageRequest.of(0, size, sort == null ? Sort.unsorted() : sort);

        return new SliceImpl<>(result, pageable, hasNext);
    }

    @Override
    public Long countAll(Long indexInfoId, LocalDate startDate, LocalDate endDate) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<IndexData> root = cq.from(IndexData.class);

        List<Predicate> predicates = new ArrayList<>();

        if(indexInfoId != null) {
            predicates.add(cb.equal(root.get("indexInfo").get("id"), indexInfoId)); // equal
        }
        if(startDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("baseDate"), startDate)); // greater
        }
        if(endDate != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("baseDate"),endDate)); // less
        }

        cq.select(cb.count(root)).where(predicates.toArray(Predicate[]::new));
        return em.createQuery(cq).getSingleResult();
    }

}
