//package com.codeit.findex.repository;
//
//import com.codeit.findex.entity.IndexData;
//import java.time.LocalDate;
//import java.util.Optional;
//import java.util.UUID;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//public interface DashboardRepository extends JpaRepository<IndexData, UUID> {
//
//  /**
//   * 특정 indexInfoId에 해당하는 가장 최신 IndexData를 조회합니다.
//   *
//   * @param indexInfoId 조회할 인덱스 정보의 ID
//   * @return 가장 최신의 IndexData 객체 (데이터가 없으면 Optional.empty())
//   */
//  Optional<IndexData> findTopByIndexInfoIdOrderByBaseDateDesc(UUID indexInfoId);
//
//  /**
//   * 특정 날짜(targetDate) 혹은 그 이전의 가장 최신 IndexData를 조회합니다. 주말이나 공휴일처럼 특정 날짜에 IndexData가 없는 경우를 처리하는 데
//   * 핵심적인 역할을 합니다.
//   *
//   * @param indexInfoId 조회할 인덱스 정보의 ID
//   * @param baseDate 기준 날짜. 이 날짜 혹은 그 이전의 IndexData를 조회합니다.
//   * @return 기준 날짜 혹은 그 이전의 가장 최신 IndexData 객체 (없으면 Optional.empty())
//   */
//  Optional<IndexData> findTopByIndexInfoIdAndBaseDateLessThanEqualOrderByBaseDateDesc(
//      UUID indexInfoId, LocalDate baseDate);
//}
