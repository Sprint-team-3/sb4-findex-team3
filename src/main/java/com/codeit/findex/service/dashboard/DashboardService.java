 package com.codeit.findex.service.dashboard;

 import com.codeit.findex.dto.dashboard.IndexInfoDto;
 import com.codeit.findex.dto.dashboard.PerformanceDto;
 import com.codeit.findex.dto.dashboard.PeriodType;
 import com.codeit.findex.entity.IndexData;
 import com.codeit.findex.repository.DashboardRepository;
 import java.time.Duration;
 import java.time.LocalDate;
 import java.util.NoSuchElementException;
 import java.util.Optional;
 import java.util.UUID;
 import lombok.RequiredArgsConstructor;
 import org.springframework.stereotype.Service;

 @Service
 @RequiredArgsConstructor
 public class DashboardService {

  private final DashboardRepository dashboardRepository;

  public PerformanceDto getPerformanceDto(IndexInfoDto i, PeriodType periodType) {
    UUID indexInfoId = i.infoId();

    IndexData current =
        findRecentIndexData(indexInfoId)
            .orElseThrow(() -> new NoSuchElementException("IndexData does not exist"));

    LocalDate currentDate = current.getBaseDate();

    Optional<IndexData> comparisonData =
        switch (periodType) {
          case DAILY ->
             findPastIndexData(
                  indexInfoId, currentDate.minus(Duration.ofDays(1)));
          case WEEKLY ->
              findPastIndexData(
                  indexInfoId, currentDate.minus(Duration.ofDays(7)));
          case MONTHLY ->
              findPastIndexData(
                  indexInfoId, currentDate.minus(Duration.ofDays(30)));
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

  public Optional<IndexData> findRecentIndexData(UUID indexInfoId) {
    return dashboardRepository.findTopByIndexInfoIdOrderByBaseDateDesc(indexInfoId);
  }

  public Optional<IndexData> findPastIndexData(UUID indexInfoId, LocalDate localDate) {

    return
 dashboardRepository.findTopByIndexInfoIdAndBaseDateLessThanEqualOrderByBaseDateDesc(indexInfoId,
 localDate);

//  }
// }
