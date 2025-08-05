package com.codeit.findex.repository;

import com.codeit.findex.entity.IndexData;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DashboardRepository extends JpaRepository<IndexData, Long> {

  // ==================================== 즐겨찾기 지수 현황 요약 ====================================
  /** 특정 indexInfoId에 해당하는 가장 최신 IndexData를 조회합니다. */
  Optional<IndexData> findTopByIndexInfoIdOrderByBaseDateDesc(long indexInfoId);

  /**
   * 특정 날짜(targetDate) 혹은 그 이전의 가장 최신 IndexData를 조회합니다. 주말이나 공휴일처럼 특정 날짜에 IndexData가 없는 경우를 처리하는 데
   * 핵심적인 역할을 합니다.
   */
  Optional<IndexData> findTopByIndexInfoIdAndBaseDateLessThanEqualOrderByBaseDateDesc(
      long indexInfoId, LocalDate baseDate);

  //  =====================
  /** 리스트에 있는 indexInfoId당 특정 indexInfoId에 해당하는 가장 최신 IndexData를 조회합니다. */
  @Query(
      "SELECT d FROM IndexData d WHERE d.indexInfo.id IN :indexInfoIds AND d.baseDate = "
          + "(SELECT MAX(d2.baseDate) FROM IndexData d2 WHERE d2.indexInfo.id = d.indexInfo.id)")
  List<IndexData> findRecentByIndexInfoIds(@Param("indexInfoIds") List<Long> indexInfoIds);

  /**
   * For a given list of IndexInfo IDs, finds all their content points that occurred on or before a
   * specified date. This is used to fetch a superset of all potential comparison content in a single
   * query.
   */
  @Query(
      "SELECT d FROM IndexData d "
          + "WHERE d.indexInfo.id IN :indexInfoIds AND d.baseDate <= :maxDate")
  List<IndexData> findPastByIndexInfoIds(
      @Param("indexInfoIds") List<Long> indexInfoIds, @Param("maxDate") LocalDate maxDate);

  // ================================================ 차트
  // ================================================
  /** 지정된 지수 정보 ID와 기준일자 범위에 해당하는 지수 데이터를 기준일자 오름차순으로 조회합니다. */
  // Asc - oldest to newest (e.g., Jan 1, Jan 2, Jan 3, ...).
  List<IndexData> findByIndexInfoIdAndBaseDateBetweenOrderByBaseDateAsc(
      long indexInfoId, LocalDate startDate, LocalDate endDate);

  // ==================================== 지수 성과 분석 랭킹 ====================================

  /**
   * 모든 IndexInfo에 대해 가장 최신의 IndexData를 한번의 쿼리로 조회합니다. N+1 문제를 해결하기 위해 Native SQL과 ROW_NUMBER를 사용합니다
   */
  @Query(
      value =
          """
        WITH RankedData AS (
            SELECT *,  -- THIS IS THE CRITICAL LINE. It makes all columns available.
                   ROW_NUMBER() OVER(PARTITION BY index_info_id ORDER BY base_date DESC) as rn
            FROM index_data
        )
        SELECT id, index_info_id, base_date, source_type, market_price,
               closing_price, high_price, low_price, versus, fluctuation_rate,
               trading_quantity, trading_price, market_total_amount, enabled,
               created_at, updated_at
        FROM RankedData
        WHERE rn = 1
      """,
      nativeQuery = true)
  List<IndexData> findAllRecentIndexData();

  /**
   * 모든 IndexInfo에 대해 특정 과거 시점 / 그 이전에 가장 최신인 IndexData를 한번의 쿼리로 조회합니다. 주말이나 공휴일 데이터를 처리하며, N+1문제를
   * 해결하기 위해 Native SQL과 ROW_NUMBER를 사용합니다.
   */
  @Query(
      value =
          """
      WITH RankedData AS (
          SELECT *,
              ROW_NUMBER() OVER(PARTITION BY index_info_id ORDER BY base_date DESC) as rn
          FROM index_data
          WHERE base_date <= :pastDate
      )
      SELECT id, index_info_id, base_date, source_type, market_price,
                              closing_price, high_price, low_price, versus, fluctuation_rate,
                              trading_quantity, trading_price, market_total_amount, enabled,
                              created_at, updated_at -- Select all columns
      FROM RankedData
      WHERE rn = 1
    """,
      nativeQuery = true)
  List<IndexData> findAllPastIndexData(@Param("pastDate") LocalDate pastDate);
}
