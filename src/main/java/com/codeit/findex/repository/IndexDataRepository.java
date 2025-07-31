package com.codeit.findex.repository;

import com.codeit.findex.entity.IndexData;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndexDataRepository extends JpaRepository<IndexData, Long> {

  Optional<IndexData> findByIndexInfoId(Long id);

  Optional<IndexData> findByIndexInfoIdAndBaseDate(long indexInfoId, LocalDate baseDate);
}
