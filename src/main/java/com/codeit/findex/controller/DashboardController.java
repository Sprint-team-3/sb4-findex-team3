package com.codeit.findex.controller;

import com.codeit.findex.dto.dashboard.ChartPeriodType;
import com.codeit.findex.dto.dashboard.IndexChartDto;
import com.codeit.findex.dto.dashboard.PerformanceDto;
import com.codeit.findex.dto.dashboard.PeriodType;
import com.codeit.findex.dto.dashboard.RankedIndexPerformanceDto;
import com.codeit.findex.service.dashboard.BasicDashboardService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DashboardController {

  private final BasicDashboardService basicDashboardService;

  public DashboardController(BasicDashboardService basicDashboardService) {
    this.basicDashboardService = basicDashboardService;
  }

  /** 즐겨찾기로 등록된 지수들의 성과를 조회합니다. (1번째 부분 - 주요 지수) */
  @GetMapping("/index-data/performance/favorite")
  public List<PerformanceDto> getFavPerformance(@RequestParam("periodType") PeriodType periodType) {
    return basicDashboardService.getFavPerformanceDto(periodType);
  }

  /** 지정된 지수 ID와 기간 유형에 해당하는 차트 데이터를 조회합니다. (2번째 부분 - 지수 차트) */
  @GetMapping("/index-data/{id}/chart")
  public IndexChartDto getChart(
      @PathVariable("id") long id, @RequestParam("periodType") ChartPeriodType periodType) {
    return basicDashboardService.getChartData(id, periodType);
  }

  /**
   * 특정 지수 정보를 기준으로 성과 순위를 조회합니다. 이 메소드는 주어진 기간과 제한된 수량에 따라 지수들의 성과 순위 목록을 반환합니다. (3번째 부분 - 지수 성과)
   */
  @GetMapping("/index-data/performance/rank")
  public List<RankedIndexPerformanceDto> getPerformanceRank(
      @RequestParam("indexInfoId") long indexInfoId,
      @RequestParam("periodType") PeriodType periodType,
      @RequestParam("limit") int limit) {
    return basicDashboardService.getPerformanceRank(indexInfoId, periodType, limit);
  }
}
