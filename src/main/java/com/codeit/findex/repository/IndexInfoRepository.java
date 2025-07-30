package com.codeit.findex.repository;

import com.codeit.findex.entity.IndexInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndexInfoRepository extends JpaRepository<IndexInfo, Long> {
    @Query("SELECT i FROM IndexInfo i WHERE i.indexName LIKE %:indexName%")
    List<IndexInfo> findByIndexName(@Param("indexName") String indexName);

    @Query("SELECT i FROM IndexInfo i WHERE i.indexClassification LIKE %:classification%")
    List<IndexInfo> findByIndexClassification(@Param("classification") String classification);

    @Query("SELECT i FROM IndexInfo i WHERE i.favorite = :favorite")
    List<IndexInfo> findByFavorite(@Param("favorite") Boolean favorite);

    @Query("")

    Optional<IndexInfo> findById(UUID id);

  Optional<IndexInfo> findByIndexName(String indexName);
}
