package com.codeit.findex.repository;

import com.codeit.findex.entity.IndexData;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DashboardRepository extends JpaRepository<IndexData, Long> {

  // 특정 indexInfoId에 해당하는 가장 최신 IndexData를 조회
  Optional<IndexData> findTopByIndexInfoIdOrderByBaseDateDesc(long indexInfoId);


  // 각 IndexInfo의 가장 최신 IndexData를 가져옴
  @Query("""
        SELECT id1 FROM IndexData id1 
        WHERE id1.indexInfo.id IN :indexInfoIds 
        AND id1.baseDate = (
            SELECT MAX(id2.baseDate) 
            FROM IndexData id2 
            WHERE id2.indexInfo.id = id1.indexInfo.id 
            AND id2.baseDate <= :maxDate
        )
        """)
  List<IndexData> findMostRecentByIndexInfoIdsAndMaxDate(
      @Param("indexInfoIds") List<Long> indexInfoIds,
      @Param("maxDate") LocalDate maxDate);

  // 각 IndexInfo의 targetDate과 가장 가까운 최신 IndexData를 가져옴
  @Query("""
        SELECT id1 FROM IndexData id1 
        WHERE id1.indexInfo.id IN :indexInfoIds 
        AND id1.baseDate = (
            SELECT MAX(id2.baseDate) 
            FROM IndexData id2 
            WHERE id2.indexInfo.id = id1.indexInfo.id 
            AND id2.baseDate <= :targetDate 
            AND id2.baseDate >= :minDate
        )
        """)
  List<IndexData> findClosestPastByIndexInfoIdsAndTargetDate(
      @Param("indexInfoIds") List<Long> indexInfoIds,
      @Param("targetDate") LocalDate targetDate,
      @Param("minDate") LocalDate minDate);


  // 지정된 지수 정보 ID와 기준일자 범위에 해당하는 지수 데이터를 기준일자 오름차순으로 조회
  // Asc - oldest to newest (e.g., Jan 1, Jan 2, Jan 3, ...).
  List<IndexData> findByIndexInfoIdAndBaseDateBetweenOrderByBaseDateAsc(
      long indexInfoId, LocalDate startDate, LocalDate endDate);

}
