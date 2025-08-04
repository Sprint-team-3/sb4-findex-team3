package com.codeit.findex.repository;

import com.codeit.findex.entity.IndexData;
import com.codeit.findex.entity.IndexInfo;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndexDataRepository extends JpaRepository<IndexData, Long> {

  Optional<IndexData> findByIndexInfoId(Long id);

  Optional<IndexData> findByIndexInfoIdAndBaseDate(long indexInfoId, LocalDate baseDate);

  boolean existsByIndexInfoAndBaseDate(IndexInfo indexInfo, LocalDate baseDate);

  Optional<IndexData> findFirstByIndexInfo_IdOrderByBaseDateDesc(Long indexInfoId);

  Page<IndexData> findAllByIndexInfoIdAndBaseDateBetweenOrderByBaseDateDesc(
      Long indexInfoId, LocalDate from, LocalDate to, Pageable pageable);

  List<IndexData> findAllByIndexInfoIdAndBaseDateIn(
      Long indexInfoId, Collection<LocalDate> baseDates);

  List<IndexData> findAllByIndexInfoIdInAndBaseDateBetween(
      Collection<Long> indexInfoIds, LocalDate from, LocalDate to);

  Optional<IndexData> findTopByIndexInfoOrderByBaseDateDesc(IndexInfo indexInfo);

  List<IndexData> findAllByIndexInfoId(long id);
}
