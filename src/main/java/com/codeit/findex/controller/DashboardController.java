 package com.codeit.findex.controller;

 import com.codeit.findex.dto.dashboard.IndexDataDto;
 import com.codeit.findex.dto.dashboard.IndexInfoDto;
 import com.codeit.findex.dto.dashboard.IndexChartDto;
 import com.codeit.findex.dto.dashboard.PerformanceDto;
 import com.codeit.findex.dto.dashboard.PeriodType;
 import com.codeit.findex.dto.dashboard.RankedIndexPerformanceDto;
 import com.codeit.findex.service.dashboard.DashboardService;
 import java.time.Duration;
 import java.time.LocalDateTime;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.UUID;
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
  public List<PerformanceDto> getPerformanceFav(@RequestParam("periodType") PeriodType periodType)
 {
    // 나중에 서비스로 바꾸기
    List<IndexInfoDto> favoriteInfoDtos = List.of(kospiInfo, kosdaqInfo);
    return favoriteInfoDtos.stream().map(i -> dashboardService.getPerformanceDto(i,
 periodType)).toList();

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
      @PathVariable("id") UUID id, @RequestParam("periodType") String periodType) {
    // search info service by id and get IndexInfo
    // IndexInfo to IndexChartDto
    return null;
  }

  // 3번째 부분 (성과)
  @GetMapping("/index-data/performance/rank")
  public List<RankedIndexPerformanceDto> getPerformanceRank(
      @RequestParam("indexInfoId") String indexInfoId,
      @RequestParam("periodType") String periodType,
      @RequestParam("limit") String limit) {
    return null;
  }

  UUID kospiId = UUID.randomUUID();
  UUID kosdaqId = UUID.randomUUID();
  // =============================  dummy data =============================
  IndexInfoDto kospiInfo =
      new IndexInfoDto(
          kospiId,
          "주가지수", // index_classification: Stock Index
          "KOSPI", // index_name
          200, // employed_items_count
          LocalDateTime.now(), // basepoint_intime
          100.00, // base_index
          "KRX", // source_type: Korea Exchange
          true // favorite
      );

  IndexInfoDto kosdaqInfo =
      new IndexInfoDto(
          kosdaqId,
          "주가지수", // index_classification: Stock Index
          "KOSDAQ", // index_name
          1500, // employed_items_count
          LocalDateTime.now(), // basepoint_intime
          1000.00, // base_index
          "KRX", // source_type: Korea Exchange
          true // favorite
      );

  private Map<UUID, List<IndexDataDto>> createDummyIndexData() {

    Map<UUID, List<IndexDataDto>> dummyIndexData = new HashMap<>();
    LocalDateTime now = LocalDateTime.now();
    List<IndexDataDto> kospiData =
        List.of(
            new IndexDataDto(UUID.randomUUID(), kospiId, now, "Open API", 2800.00), // Today
            new IndexDataDto(
                UUID.randomUUID(),
                kospiId,
                now.minus(Duration.ofDays(1)),
                "Open API",
                2795.50), // Yesterday
            // Last Week
            new IndexDataDto(
                UUID.randomUUID(), kospiId, now.minus(Duration.ofDays(4)), "Open API", 2705.25),
            new IndexDataDto(
                UUID.randomUUID(), kospiId, now.minus(Duration.ofDays(7)), "Open API", 2815.20),
            // last month
            new IndexDataDto(
                UUID.randomUUID(), kospiId, now.minus(Duration.ofDays(10)), "Open API", 2650.75),
            new IndexDataDto(
                UUID.randomUUID(), kospiId, now.minus(Duration.ofDays(26)), "Open API", 2650.75),
            new IndexDataDto(
                UUID.randomUUID(),
                kospiId,
                now.minus(Duration.ofDays(31)),
                "Open API",
                2750.75) // Last Month
        );

    List<IndexDataDto> kosdaqData =
        List.of(
            new IndexDataDto(UUID.randomUUID(), kosdaqId, now, "Open API", 2800.00), // Today
            new IndexDataDto(
                UUID.randomUUID(),
                kosdaqId,
                now.minus(Duration.ofDays(1)),
                "Open API",
                2795.50), // Yesterday
            // Last Week
            new IndexDataDto(
                UUID.randomUUID(), kosdaqId, now.minus(Duration.ofDays(3)), "Open API", 2919.90),
            new IndexDataDto(
                UUID.randomUUID(), kosdaqId, now.minus(Duration.ofDays(7)), "Open API", 2815.20),
            // Last Month
            new IndexDataDto(
                UUID.randomUUID(), kosdaqId, now.minus(Duration.ofDays(20)), "Open API", 2620.45),
            new IndexDataDto(
                UUID.randomUUID(), kosdaqId, now.minus(Duration.ofDays(31)), "Open API",
 2750.75));

    dummyIndexData.put(kospiId, kospiData);
    dummyIndexData.put(kosdaqId, kosdaqData);

    return dummyIndexData;
//  }
//
// }
