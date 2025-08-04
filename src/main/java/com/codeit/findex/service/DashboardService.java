package com.codeit.findex.service;

import com.codeit.findex.dto.dashboard.ChartPeriodType;
import com.codeit.findex.dto.dashboard.IndexChartDto;
import com.codeit.findex.dto.dashboard.PerformanceDto;
import com.codeit.findex.dto.dashboard.PeriodType;
import com.codeit.findex.dto.dashboard.RankedIndexPerformanceDto;
import java.util.List;

public interface DashboardService {

  /** 사용자가 즐겨찾기한 지수들의 성과 정보를 조회합니다. 성과는 지정된 기간(periodType) 동안의 종가를 기준으로 비교하여 계산됩니다. */
  List<PerformanceDto> getFavPerformanceDto(PeriodType periodType);

  /** 특정 지수의 차트 데이터를 조회합니다. 월/분기/년 단위의 시계열 데이터는 종가를 기준으로 하며, 5일, 20일 이동평균선 데이터를 포함합니다. */
  IndexChartDto getChartData(long indexInfoId, ChartPeriodType chartPeriodType);

  /** 지정된 기간 동안의 지수 성과 순위를 조회합니다. 전일/전주/전월 대비 성과를 종가 기준으로 비교하여, 상위 (limit)개의 순위 목록을 반환합니다. */
  List<RankedIndexPerformanceDto> getPerformanceRank(
      long indexInfoId, PeriodType periodType, int limit);
}
