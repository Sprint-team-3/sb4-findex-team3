package com.codeit.findex.repository;

import com.codeit.findex.entity.IndexInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndexInfoRepository extends JpaRepository<IndexInfo, Long> {
    @Query("SELECT i FROM IndexInfo i WHERE i.indexName LIKE %:indexName%")
    List<IndexInfo> findByIndexName(@Param("indexName") String indexName);

    @Query("SELECT i FROM IndexInfo i WHERE i.indexClassification LIKE %:classification%")
    List<IndexInfo> findByIndexClassification(@Param("classification") String classification);

    @Query("SELECT i FROM IndexInfo i WHERE i.favorite = :favorite")
    List<IndexInfo> findByFavorite(@Param("favorite") Boolean favorite);

    @Query("SELECT i FROM IndexInfo i WHERE i.indexName LIKE %:indexName% " +
            "AND i.indexClassification LIKE %:classification%")
    List<IndexInfo> findByIndexNameAndIndexClassification(@Param("indexName") String indexName, @Param("classification") String classification);

    @Query("SELECT i FROM IndexInfo i WHERE i.indexName LIKE %:indexName% " +
            "AND i.favorite = :favorite")
    List<IndexInfo> findByIndexNameAndFavorite(@Param("indexName") String indexName, @Param("favorite") Boolean favorite);

    @Query("SELECT i FROM IndexInfo i WHERE i.indexClassification LIKE %:classification% " +
            "AND i.favorite = :favorite")
    List<IndexInfo> findByIndexClassificationAndFavorite(@Param("classification") String classification, @Param("favorite") Boolean favorite);

    @Query("SELECT i FROM IndexInfo i WHERE i.indexName LIKE %:indexName% " +
            "AND i.indexClassification LIKE %:classification% " +
            "AND i.favorite = :favorite")
    List<IndexInfo> findByIndexNameAndIndexClassificationAndFavorite(
            @Param("indexName") String indexName,
            @Param("classification") String classification,
            @Param("favorite") Boolean favorite);


  //Optional<IndexInfo> findByIndexName(String indexName);

  Optional<IndexInfo> findByIndexClassificationAndIndexName(
      String indexClassification, String indexName);
}
