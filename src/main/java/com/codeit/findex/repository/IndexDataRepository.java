package com.codeit.findex.repository;

import com.codeit.findex.entity.IndexData;
import com.codeit.findex.entity.IndexInfo;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IndexDataRepository extends JpaRepository<IndexData, Long> {

  // 지수 데이터 등록
  //    if(dataRepository.exists(request.indexInfo()) && dataRepository.exists(request.baseDate()))
  // {}
  boolean existsByIndexInfoIdAndBaseDate(Long indexInfoId, LocalDate baseDate);

  // 지수 데이터 목록 조회
  Page<IndexData> findByIndexInfoAndBaseDateBetween(
      IndexInfo indexInfo, LocalDate startDate, LocalDate endDate, Pageable pageable);

  // BasicIntegrationService에서 사용하는 메서드입니다.
  Optional<IndexData> findByIndexInfoIdAndBaseDate(long id, LocalDate localDate);

  Optional<IndexData> findTopByIndexInfoOrderByBaseDateDesc(IndexInfo indexInfo);

  List<IndexData> findAllByIndexInfoId(Long id);

  // {지수 분류명}, {지수명}, {채용 종목 수}로 정렬 및 페이지네이션을 구현합니다.
  @Query("""
        SELECT d FROM IndexData d
        WHERE (:indexInfoId IS NULL OR d.indexInfo.id = :indexInfoId)
          AND (:startDate IS NULL OR d.baseDate >= :startDate)
          AND (:endDate IS NULL OR d.baseDate <= :endDate)
    """)
  Slice<IndexData> findByConditionsWithCursor(
          @Param("indexInfoId") Long indexInfoId,
          @Param("startDate") String startDate,
          @Param("endDate") String endDate,
          Pageable pageable
  );
}
