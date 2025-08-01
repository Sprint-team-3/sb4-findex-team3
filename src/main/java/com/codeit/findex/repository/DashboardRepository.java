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
  /**
   * 특정 indexInfoId에 해당하는 가장 최신 IndexData를 조회합니다.
   *
   * @param indexInfoId 조회할 인덱스 정보의 ID
   * @return 가장 최신의 IndexData 객체 (데이터가 없으면 Optional.empty())
   */
  Optional<IndexData> findTopByIndexInfoIdOrderByBaseDateDesc(long indexInfoId);

  /**
   * 특정 날짜(targetDate) 혹은 그 이전의 가장 최신 IndexData를 조회합니다. 주말이나 공휴일처럼 특정 날짜에 IndexData가 없는 경우를 처리하는 데
   * 핵심적인 역할을 합니다.
   *
   * @param indexInfoId 조회할 인덱스 정보의 ID
   * @param baseDate 기준 날짜. 이 날짜 혹은 그 이전의 IndexData를 조회합니다.
   * @return 기준 날짜 혹은 그 이전의 가장 최신 IndexData 객체 (없으면 Optional.empty())
   */
  Optional<IndexData> findTopByIndexInfoIdAndBaseDateLessThanEqualOrderByBaseDateDesc(
      long indexInfoId, LocalDate baseDate);

  // ==================================== 차트 ====================================
  /**
   * 지정된 지수 정보 ID와 기준일자 범위에 해당하는 지수 데이터를 기준일자 오름차순으로 조회합니다.
   *
   * @param indexInfoId 지수 정보의 고유 ID
   * @param startDate 조회 시작일자
   * @param endDate 조회 종료일자
   * @return 기준일자 오름차순으로 정렬된 {@link IndexData} 객체 리스트
   */
  // Asc - oldest to newest (e.g., Jan 1, Jan 2, Jan 3, ...).
  List<IndexData> findByIndexInfoIdAndBaseDateBetweenOrderByBaseDateAsc(
      long indexInfoId, LocalDate startDate, LocalDate endDate);

  // ==================================== 지수 성과 분석 랭킹 ====================================

  /**
   * 모든 IndexInfo에 대해 가장 최신의 IndexData를 한번의 쿼리로 조회합니다. N+1 문제를 해결하기 위해 Native SQL과 ROW_NUMBER를 사용합니다
   *
   * @return 각 지수별 가장 최신 IndexData 객체의 리스트
   */
  @Query(
      value =
          """
        WITH RankedData AS (
            SELECT id, indexInfoId, base_date, closing_price,
                   ROW_NUMBER() OVER(PARTITION BY indexInfoId ORDER BY base_date DESC) as rn
            FROM IndexData
        )
        SELECT id, indexInfoId, base_date, closing_price
        FROM RankedData
        WHERE rn = 1
      """,
      nativeQuery = true)
  List<IndexData> findAllRecentIndexData();

  /**
   * 모든 IndexInfo에 대해 특정 과거 시점 / 그 이전에 가장 최신인 IndexData를 한번의 쿼리로 조회합니다. 주말이나 공휴일 데이터를 처리하며, N+1문제를
   * 해결하기 위해 Native SQL과 ROW_NUMBER를 사용합니다.
   *
   * @param pastDate 조회 기준이 되는 과거 날짜. 이 날짜 혹은 그 이전의 데이터를 찾습니다.
   * @return 각 지수별로, 기준 날짜 혹은 그 이전의 가장 최신 IndexData 객체의 리스트
   */
  @Query(
      value =
          """
      WITH RankedData AS (
          SELECT id, indexInfoId, base_date, closing_price,
              ROW NUMBER() OVER(PARTITION BY indexInfoId ORDER BY base_date DESC) as rn
          FROM IndexData
          WHERE base_date <= :pastDate
      )
      SELECT id, indexInfoId, base_date, closing_price
      FROM RankedData
      WHERE rn = 1
    """,
      nativeQuery = true)
  List<IndexData> findAllPastIndexData(@Param("pastDate") LocalDate pastDate);


}
