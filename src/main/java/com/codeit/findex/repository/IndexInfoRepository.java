package com.codeit.findex.repository;

import com.codeit.findex.entity.IndexInfo;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IndexInfoRepository extends JpaRepository<IndexInfo, Long> {

  @Query(value = """
          SELECT i FROM IndexInfo i
          WHERE (:indexName IS NULL OR i.indexName LIKE CONCAT('%', CAST(:indexName AS string), '%'))
          AND (:classification IS NULL OR i.indexClassification LIKE CONCAT('%', CAST(:classification AS string), '%'))
          AND (:favorite IS NULL OR i.favorite = :favorite)
          AND (:idAfter IS NULL OR i.id > :idAfter)
          """)
  Slice<IndexInfo> findBySearchCondWithPaging(
          @Param("indexName") String indexName,
          @Param("classification") String classification,
          @Param("favorite") Boolean favorite,
          @Param("idAfter") Long idAfter,
          Pageable pageable);

  @Query("""
    SELECT COUNT(i)
    FROM IndexInfo i
    WHERE (:indexName IS NULL or i.indexName LIKE %:indexName%)
    AND (:classification IS NULL OR i.indexClassification LIKE %:classification%)
    AND (:favorite IS NULL OR i.favorite = :favorite)
""")
  long countBySearchCond(
          @Param("indexName") String indexName,
          @Param("classification") String classification,
          @Param("favorite") Boolean favorite
  );

  List<IndexInfo> findAll();

  Optional<IndexInfo> findById(long id);

  boolean existsByIndexName(String indexName);

  List<IndexInfo> findAllByFavorite(Boolean favorite);
}
