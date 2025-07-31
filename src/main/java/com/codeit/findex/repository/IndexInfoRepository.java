package com.codeit.findex.repository;

import com.codeit.findex.entity.IndexInfo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndexInfoRepository extends JpaRepository<IndexInfo, Long> {

  Optional<IndexInfo> findByIndexName(String indexName);
}
