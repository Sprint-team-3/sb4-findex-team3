package com.codeit.findex.repository;

import com.codeit.findex.entity.IndexInfo;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndexInfoRepository extends JpaRepository<IndexInfo, Long> {

  Optional<IndexInfo> findByIndexName(String indexName);

  Optional<IndexInfo> findByIndexClassificationAndIndexName(
      String indexClassification, String indexName);

  Optional<IndexInfo> findByIndexClassificationAndIndexNameAndBasepointInTime(
      String indexClassification, String indexName, LocalDate basepointInTime);

  List<IndexInfo> findAllByEnabledTrue();
}
