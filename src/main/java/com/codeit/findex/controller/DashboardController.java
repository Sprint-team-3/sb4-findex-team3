package com.codeit.findex.controller;

import com.codeit.findex.dto.IndexDataDto;
import com.codeit.findex.dto.IndexInfoDto;
import com.codeit.findex.dto.dashboard.ChartPeriodType;
import com.codeit.findex.dto.dashboard.IndexChartDto;
import com.codeit.findex.dto.dashboard.PerformanceDto;
import com.codeit.findex.dto.dashboard.PeriodType;
import com.codeit.findex.dto.dashboard.RankedIndexPerformanceDto;
import com.codeit.findex.entityEnum.SourceType;
import com.codeit.findex.service.dashboard.DashboardService;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DashboardController {

  private final DashboardService dashboardService;

  public DashboardController(DashboardService dashboardService) {
    this.dashboardService = dashboardService;
  }

  /**
   * 즐겨찾기로 등록된 지수들의 성과를 조회합니다. (1번째 부분)
   *
   * @param periodType 성과 기간 유형 (DAILY, WEEKLY, MONTHLY)
   * @return PerformanceDto 리스트
   */
  @GetMapping("/index-data/performance/favorite")
  public List<PerformanceDto> getFavPerformance(
      @RequestParam("periodType") PeriodType periodType) {
    // 나중에 서비스로 바꾸기
    List<IndexInfoDto> favoriteInfoDtos = List.of(kospiInfo, kosdaqInfo);
    return favoriteInfoDtos.stream()
        .map(i -> dashboardService.getFavPerformanceDto(i, periodType))
        .filter(Objects::nonNull) // NPE
        .toList();

    //    Map<UUID, List<IndexDataDto>> dummyIndexData = createDummyIndexData();
    //          List<IndexDataDto> indexDataDto = dummyIndexData.get(indexInfoId);

    // dummy data 사용
    //    return favoriteInfoDtos.stream()
    //        .map(i -> {
    //          UUID indexInfoId = i.infoId;
    //          List<IndexDataDto> indexDataDto = dummyIndexData.get(indexInfoId);
    //
    //          IndexDataDto current = indexDataDto.get(0);
    //          IndexDataDto comparisonData = switch (periodType) {
    //            case DAILY -> indexDataDto.get(1);
    //            case WEEKLY -> indexDataDto.get(2);
    //            case MONTHLY -> indexDataDto.get(3);
    //
    //          };
    //
    //          double currentPrice = current.closingPrice; // 증가
    //          double beforePrice = comparisonData.closingPrice;
    //          double versus = currentPrice - beforePrice;
    //          double fluctuationRate = versus / beforePrice * 100;
    //
    //          return new PerformanceDto(
    //              indexInfoId,
    //              i.indexClassification,
    //              i.indexName,
    //              versus,
    //              fluctuationRate,
    //              currentPrice,
    //              beforePrice
    //          );
    //        })
    //        .toList();
  }

  // 2번째 부분 (차트)
  @GetMapping("/index-data/{id}/chart")
  public IndexChartDto getChart(
      @PathVariable("id") long id, @RequestParam("periodType") ChartPeriodType periodType) {
    return dashboardService.getChartData(id, periodType);
  }

  // 3번째 부분 (성과)
  @GetMapping("/index-data/performance/rank")
  public List<RankedIndexPerformanceDto> getPerformanceRank(
      @RequestParam("indexInfoId") long indexInfoId,
      @RequestParam("periodType") PeriodType periodType,
      @RequestParam("limit") int limit) {
    return dashboardService.getPerformanceRank(indexInfoId, periodType, limit);
  }

  // =============================  dummy data =============================

  long kospiId = 1L;
  long kosdaqId = 2L;
  IndexInfoDto kospiInfo =
      new IndexInfoDto(
          kospiId,
          "주가지수", // index_classification: Stock Index
          "KOSPI", // index_name
          200, // employed_items_count
          LocalDate.now(), // basepoint_intime
          100.00, // base_index
          SourceType.USER, // source_type
          true // favorite
          );

  IndexInfoDto kosdaqInfo =
      new IndexInfoDto(
          kosdaqId,
          "주가지수", // index_classification: Stock Index
          "KOSDAQ", // index_name
          1500, // employed_items_count
          LocalDate.now(), // basepoint_intime
          1000.00, // base_index
          SourceType.USER, // source_type
          true // favorite
          );

  private Map<Long, List<IndexDataDto>> createDummyIndexData() {

    Map<Long, List<IndexDataDto>> dummyIndexData = new HashMap<>();
    LocalDate now = LocalDate.now();
    List<IndexDataDto> kospiData =
        List.of(
            new IndexDataDto(
                1L, kospiId, now, SourceType.USER, 2800.00, 0, 0, 0, 0, 0, 0, 0, 0), // Today
            new IndexDataDto(
                2L,
                kospiId,
                now.minus(Duration.ofDays(1)),
                SourceType.OPEN_API,
                2795.50,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0), // Yesterday
            // Last Week
            new IndexDataDto(
                3L,
                kospiId,
                now.minus(Duration.ofDays(4)),
                SourceType.OPEN_API,
                2115.11,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0),
            new IndexDataDto(
                4L,
                kospiId,
                now.minus(Duration.ofDays(7)),
                SourceType.OPEN_API,
                2892.57,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0),
            // last month
            new IndexDataDto(
                5L,
                kospiId,
                now.minus(Duration.ofDays(10)),
                SourceType.OPEN_API,
                2119.50,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0),
            new IndexDataDto(
                6L,
                kospiId,
                now.minus(Duration.ofDays(26)),
                SourceType.OPEN_API,
                2000.50,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0),
            new IndexDataDto(
                7L,
                kospiId,
                now.minus(Duration.ofDays(31)),
                SourceType.OPEN_API,
                2900.50,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0) // Last Month
            );

    List<IndexDataDto> kosdaqData =
        List.of(
            new IndexDataDto(
                8L, kosdaqId, now, SourceType.OPEN_API, 2795.50, 0, 0, 0, 0, 0, 0, 0, 0), // Today
            new IndexDataDto(
                9L,
                kosdaqId,
                now.minus(Duration.ofDays(1)),
                SourceType.OPEN_API,
                2800.16,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0), // Yesterday
            // Last Week
            new IndexDataDto(
                10L,
                kosdaqId,
                now.minus(Duration.ofDays(3)),
                SourceType.OPEN_API,
                2335.50,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0),
            new IndexDataDto(
                11L,
                kosdaqId,
                now.minus(Duration.ofDays(7)),
                SourceType.OPEN_API,
                2225.50,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0),
            // Last Month
            new IndexDataDto(
                12L,
                kosdaqId,
                now.minus(Duration.ofDays(20)),
                SourceType.OPEN_API,
                2999.50,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0),
            new IndexDataDto(
                13L,
                kosdaqId,
                now.minus(Duration.ofDays(31)),
                SourceType.OPEN_API,
                2111.50,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0));

    dummyIndexData.put(kospiId, kospiData);
    dummyIndexData.put(kosdaqId, kosdaqData);

    return dummyIndexData;
  }
}
