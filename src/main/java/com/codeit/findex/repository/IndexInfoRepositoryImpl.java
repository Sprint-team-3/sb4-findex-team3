package com.codeit.findex.repository;

import com.codeit.findex.dto.indexInfo.request.IndexInfoSearchCond;
import com.codeit.findex.dto.indexInfo.response.IndexInfoDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class IndexInfoRepositoryImpl implements IndexInfoRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<IndexInfoDto> findBySearchCond (IndexInfoSearchCond cond) {
        StringBuilder jpql = new StringBuilder("SELECT i FROM IndexInfo i WHERE 1=1");

        if (cond.getIndexName() != null) {
            jpql.append(" AND i.indexName LIKE :indexName");
    }
        if (cond.getIndexClassification() != null) {
            jpql.append(" AND i.indexClassification LIKE :indexClassification");
        }
        if (cond.getFavorite() != null) {
            jpql.append(" AND i.favorite = :favorite");
        }

        TypedQuery<IndexInfoDto> query = em.createQuery(jpql.toString(), IndexInfoDto.class);
        if (cond.getIndexName() != null) {
            query.setParameter("indexName", "%" + cond.getIndexName() + "%");
        }
        if (cond.getIndexClassification() != null) {
            query.setParameter("indexClassification", "%" + cond.getIndexClassification() + "%");
        }
        if (cond.getFavorite() != null) {
            query.setParameter("favorite", cond.getFavorite());
        }
        query.setMaxResults(cond.getSize());

        return query.getResultList();
    }
}
