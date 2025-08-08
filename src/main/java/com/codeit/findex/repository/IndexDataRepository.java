package com.codeit.findex.repository;

import com.codeit.findex.entity.IndexData;
import com.codeit.findex.entity.IndexInfo;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.codeit.findex.repository.custom.IndexDataRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IndexDataRepository extends JpaRepository<IndexData, Long>, IndexDataRepositoryCustom {
  // 지수 데이터 등록
  //    if(dataRepository.exists(request.indexInfo()) && dataRepository.exists(request.baseDate()))
  // {}
  boolean existsByIndexInfoIdAndBaseDate(Long indexInfoId, LocalDate baseDate);

  // 지수 데이터 목록 조회
  Page<IndexData> findByIndexInfoAndBaseDateBetween(
      IndexInfo indexInfo, LocalDate startDate, LocalDate endDate, Pageable pageable);

  @Query("SELECT d FROM IndexData d WHERE d.indexInfo = :indexInfo AND d.baseDate IN :baseDates")
  List<IndexData> findAllByIndexInfoAndBaseDateIn(@Param("indexInfo") IndexInfo indexInfo, @Param("baseDates") List<LocalDate> baseDates);

  Optional<IndexData> findTopByIndexInfoOrderByBaseDateDesc(IndexInfo indexInfo);

  List<IndexData> findAllByIndexInfoId(Long id);




  // 추가된 쿼리, indexInfoId가 있다면
//  @Query("""
//       SELECT d FROM IndexData d
//       WHERE (:indexInfoId IS NULL OR d.indexInfo.id = :indexInfoId)
//        AND(:startDate IS NULL OR d.baseDate >= :startDate)
//        AND(:endDate IS NULL OR d.baseDate <= :endDate)
//        AND(:idAfter IS NULL OR d.id > :idAfter)
//  """)
//  Slice<IndexData> findByConditionsWithCursor(
//          @Param("indexInfoId") Long indexInfoId,
//          @Param("startDate") LocalDate startDate,
//          @Param("endDate") LocalDate endDate,
//          @Param("idAfter") Long idAfter,
//          Pageable pageable
//  );
//
//  // indexInfoId가 없다면, String을 LocalDate로 변경함
//  @Query("""
//        SELECT d FROM IndexData d
//        WHERE(:startDate IS NULL OR d.baseDate >= :startDate)
//        AND(:endDate IS NULL OR d.baseDate <= :endDate)
//  """)
//  Slice<IndexData> findAllByDateRangeWithCursor(
//          @Param("startDate") LocalDate startDate,
//          @Param("endDate") LocalDate endDate,
//          Pageable pageable
//  );

    // 추가된 쿼리

//  // {지수 분류명}, {지수명}, {채용 종목 수}로 정렬 및 페이지네이션을 구현합니다.
//  @Query("""
//        SELECT d FROM IndexData d
//        WHERE (:indexInfoId IS NULL OR d.indexInfo.id = :indexInfoId)
//          AND (:startDate IS NULL OR d.baseDate >= :startDate)
//          AND (:endDate IS NULL OR d.baseDate <= :endDate)
//          AND (:idAfter IS NULL OR d.id > :idAfter)
//    """)
//  Slice<IndexData> findByConditionsWithCursor(
//          @Param("indexInfoId") Long indexInfoId,
//          @Param("startDate") LocalDate startDate,
//          @Param("endDate") LocalDate endDate,
//          @Param("idAfter") Long idAfter, // 이걸 서치할 수 있어야 페이지네이션이 정상 작동한다
//          Pageable pageable
//  );

  // 전체 요소 개수 조회
  Long countByIndexInfoId(Long indexInfoId);

  //
  // @Query("SELECT d FROM IndexData d WHERE d.baseDate BETWEEN :startDate AND :endDate")
  // New method for when ID is NOT present
//  @Query("SELECT d FROM IndexData d WHERE (:startDate IS NULL OR d.baseDate >= :startDate) AND (:endDate IS NULL OR d.baseDate <= :endDate)")
//  Slice<IndexData> findAllByDateRangeWithCursor(@Param("startDate") LocalDate startDate,
//                                                @Param("endDate") LocalDate endDate,
//                                                Pageable pageable);

  // @Query("SELECT count(d) FROM IndexData d WHERE d.baseDate BETWEEN :startDate AND :endDate")
  // New count method for when ID is NOT present
  @Query("SELECT count(d) FROM IndexData d WHERE (:startDate IS NULL OR d.baseDate >= :startDate) AND (:endDate IS NULL OR d.baseDate <= :endDate)")
  Long countByDateRange(@Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

  // CSV에 사용되는 메서드
  @Query("""
    SELECT d FROM IndexData d
    WHERE (:indexInfoId IS NULL OR d.indexInfo.id = :indexInfoId)
      AND d.baseDate >= COALESCE(:startDate, d.baseDate)
      AND d.baseDate <= COALESCE(:endDate, d.baseDate)
    ORDER BY d.baseDate DESC
""")
  List<IndexData> findByFilters(@Param("indexInfoId") Long indexInfoId,
                                               @Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate,
                                               @Param("sortField") String sortField,
                                               @Param("sortDirection") String sortDirection);
}

/*
@Query("""
    SELECT d FROM IndexData d
    WHERE (:indexInfoId IS NULL OR d.indexInfo.id = :indexInfoId)
      AND (:startDate IS NULL OR d.baseDate >= :startDate)
      AND (:endDate IS NULL OR d.baseDate <= :endDate)
    ORDER BY
      CASE WHEN :sortField = 'baseDate' AND :sortDirection = 'DESC' THEN d.baseDate END DESC,
      CASE WHEN :sortField = 'baseDate' AND :sortDirection = 'ASC' THEN d.baseDate END ASC
""")
 */