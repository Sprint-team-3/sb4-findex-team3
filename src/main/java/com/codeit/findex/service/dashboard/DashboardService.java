package com.codeit.findex.service.dashboard;

import com.codeit.findex.dto.IndexInfoDto;
import com.codeit.findex.dto.dashboard.ChartDataPoint;
import com.codeit.findex.dto.dashboard.ChartPeriodType;
import com.codeit.findex.dto.dashboard.IndexChartDto;
import com.codeit.findex.dto.dashboard.PerformanceDto;
import com.codeit.findex.dto.dashboard.PeriodType;
import com.codeit.findex.entity.IndexData;
import com.codeit.findex.entityEnum.SourceType;
import com.codeit.findex.repository.DashboardRepository;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

  private final DashboardRepository dashboardRepository;

  public IndexChartDto getChartData(long indexInfoId, ChartPeriodType chartPeriodType) {

    IndexInfoDto indexInfoDto =
        new IndexInfoDto(
            1L,
            "주가지수", // index_classification: Stock Index
            "KOSPI", // index_name
            200, // employed_items_count
            LocalDate.now(), // basepoint_intime
            100.00, // base_index
            SourceType.USER, // source_type
            true // favorite
            );
    //    IndexInfo current =
    //        indexInfoRepository.find(indexInfoId)
    //            .orElseThrow(() -> new NoSuchElementException("IndexData does not exist"));

    // fetches the latest IndexData
    IndexData latestIndexData =
        findRecentIndexData(indexInfoId)
            .orElseThrow(() -> new NoSuchElementException("IndexData does not exist"));

    // get the latest date and startDate based on chartPeriodType
    LocalDate endDate = latestIndexData.getBaseDate();
    LocalDate startDate =
        switch (chartPeriodType) {
          case MONTHLY -> endDate.minusMonths(1L);
          case QUARTERLY -> endDate.minusMonths(3L);
          case YEARLY -> endDate.minusYears(1L);
        };

    // Fetching *ALL* historical IndexData from 30 days before startDate to endDate
    // Data buffer for sliding window
    List<IndexData> indexDataList =
        findRangeIndexData(
            indexInfoId,
            startDate.minusDays(30L), // one month before
            endDate);

    List<ChartDataPoint> dataPoints = new ArrayList<>();
    List<ChartDataPoint> ma5DataPoints = new ArrayList<>();
    List<ChartDataPoint> ma20DataPoints = new ArrayList<>();

    for (int i = 0; i < indexDataList.size(); i++) {

      IndexData indexData = indexDataList.get(i);
      LocalDate currentDate = indexData.getBaseDate();
      // 지수 - blue line
      if (currentDate.isAfter(startDate) || currentDate.equals(startDate)) {
        dataPoints.add(new ChartDataPoint(indexData.getBaseDate(), indexData.getClosingPrice()));

        // 5일 이동평균선
        if (i >= 4) {
          double closingPricetotal = 0;
          for (int j = 0; j < 5; j++) {
            closingPricetotal += indexDataList.get(i - j).getClosingPrice();
          }
          closingPricetotal /= 5;
          ma5DataPoints.add(new ChartDataPoint(indexData.getBaseDate(), closingPricetotal));
        }

        // 20일 이동평균선
        if (i >= 19) {
          double closingPricetotal = 0;
          for (int j = 0; j < 20; j++) {
            closingPricetotal += indexDataList.get(i - j).getClosingPrice();
          }
          closingPricetotal /= 20;
          ma20DataPoints.add(new ChartDataPoint(indexData.getBaseDate(), closingPricetotal));
        }
      }
    }

    return new IndexChartDto(
        indexInfoId,
        indexInfoDto.indexClassification(),
        indexInfoDto.indexName(),
        chartPeriodType,
        dataPoints,
        ma5DataPoints,
        ma20DataPoints);
  }

  public PerformanceDto getPerformanceDto(IndexInfoDto i, PeriodType periodType) {
    long indexInfoId = i.id();

    IndexData current =
        findRecentIndexData(indexInfoId)
            .orElseThrow(() -> new NoSuchElementException("IndexData does not exist"));

    LocalDate currentDate = current.getBaseDate();

    Optional<IndexData> comparisonData =
        switch (periodType) {
          case DAILY -> findPastIndexData(indexInfoId, currentDate.minus(Duration.ofDays(1)));
          case WEEKLY -> findPastIndexData(indexInfoId, currentDate.minus(Duration.ofDays(7)));
          case MONTHLY -> findPastIndexData(indexInfoId, currentDate.minus(Duration.ofDays(30)));
        };

    // *** GRACEFUL HANDLING ***
    if (comparisonData.isEmpty()) {
      return null;
    }

    double currentPrice = current.getClosingPrice(); // 증가
    double beforePrice = comparisonData.get().getClosingPrice();
    double versus = currentPrice - beforePrice;
    double fluctuationRate = versus / beforePrice * 100;

    return new PerformanceDto(
        indexInfoId,
        i.indexClassification(),
        i.indexName(),
        versus,
        fluctuationRate,
        currentPrice,
        beforePrice);
  }

  // private methods

  private Optional<IndexData> findRecentIndexData(long indexInfoId) {
    return dashboardRepository.findTopByIndexInfoIdOrderByBaseDateDesc(indexInfoId);
  }

  private Optional<IndexData> findPastIndexData(long indexInfoId, LocalDate localDate) {
    return dashboardRepository.findTopByIndexInfoIdAndBaseDateLessThanEqualOrderByBaseDateDesc(
        indexInfoId, localDate);
  }

  // chart
  private List<IndexData> findRangeIndexData(
      long indexInfoId, LocalDate startDate, LocalDate endDate) {
    return dashboardRepository.findByIndexInfoIdAndBaseDateBetweenOrderByBaseDateAsc(
        indexInfoId, startDate, endDate);
  }
}
