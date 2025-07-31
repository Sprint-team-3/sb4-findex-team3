package com.codeit.findex.repository;

import com.codeit.findex.entity.IndexData;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndexDataRepository extends JpaRepository<IndexData, Long> {

  Optional<IndexData> findByIndexInfoId(Long id);

  List<IndexData> findByIndexInfoIndexName(String indexInfoIndexName);
}
