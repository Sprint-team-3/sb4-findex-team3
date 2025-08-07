package com.codeit.findex.service.dashboard.basic;

import com.codeit.findex.dto.dashboard.response.ChartDataPoint;
import com.codeit.findex.dto.dashboard.response.ChartPeriodType;
import com.codeit.findex.dto.dashboard.response.IndexChartDto;
import com.codeit.findex.dto.dashboard.response.PerformanceDto;
import com.codeit.findex.dto.dashboard.response.PeriodType;
import com.codeit.findex.dto.dashboard.response.RankedIndexPerformanceDto;
import com.codeit.findex.dto.indexInfo.response.IndexInfoDto;
import com.codeit.findex.entity.IndexData;
import com.codeit.findex.entity.IndexInfo;
import com.codeit.findex.repository.DashboardRepository;
import com.codeit.findex.repository.IndexInfoRepository;
import com.codeit.findex.service.indexinfo.IndexInfoService;
import com.codeit.findex.service.dashboard.DashboardService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicDashboardService implements DashboardService {

  private final IndexInfoRepository indexInfoRepository;
  private final DashboardRepository dashboardRepository;

  private final IndexInfoService indexInfoService;

  /** 주요 지수 */
  @Override
  public List<PerformanceDto> getFavPerformanceDto(PeriodType periodType) {

    // 즐겨 찾기한 IndexData리스트 가져오기
    List<IndexInfoDto> favoriteInfos = indexInfoService.findAllByFavorite(true);
    // 위의 리스트 토대로 id 리스트 생성
    List<Long> indexInfoIdList = favoriteInfos.stream().map(IndexInfoDto::getId).toList();

    LocalDate currentDate = LocalDate.now();
    LocalDate pastDate = calculateMinusDate(periodType);

    // 날짜에 맞춰서 가져올 수 있는 가장 가까운 날짜의 IndexData
    List<IndexData> currentDataList = dashboardRepository
        .findMostRecentByIndexInfoIdsAndMaxDate(indexInfoIdList, currentDate);

    List<IndexData> pastDataList = dashboardRepository
        .findClosestPastByIndexInfoIdsAndTargetDate(
            indexInfoIdList,
            pastDate,
            pastDate.minusDays(30)); // 30일 전

    // 시간복잡도 O(1)을 위해 Map으로 변환
    Map<Long, IndexData> currentDataMap = currentDataList.stream()
        .collect(Collectors.toMap(data -> data.getIndexInfo().getId(), Function.identity()));

    Map<Long, IndexData> pastDataMap = pastDataList.stream()
        .collect(Collectors.toMap(data -> data.getIndexInfo().getId(), Function.identity()));


    List<PerformanceDto> performanceDtoList = new ArrayList<>();
    for (IndexInfoDto i : favoriteInfos) {
      long indexInfoId = i.getId();

      IndexData current = currentDataMap.get(indexInfoId);
      IndexData past = pastDataMap.get(indexInfoId);

      // 둘중 하나라도 없으면 스킵
      if (current == null || past == null) {
        continue;
      }

      double currentPrice = current.getClosingPrice(); // 증가
      double beforePrice = past.getClosingPrice();
      double versus = currentPrice - beforePrice;
      double fluctuationRate = 0.0;
      if (beforePrice != 0) {
        fluctuationRate = versus / beforePrice * 100;
      }

      performanceDtoList.add(
          new PerformanceDto(
              indexInfoId,
              i.getIndexClassification(),
              i.getIndexName(),
              versus,
              fluctuationRate,
              currentPrice,
              beforePrice));
    }
    return performanceDtoList;
  }

  /** 지수 차트 */
  @Override
  public IndexChartDto getChartData(Long indexInfoId, ChartPeriodType chartPeriodType) {

    if (indexInfoId == null ) {
      return null;
    }

    IndexInfo indexInfo =
        indexInfoRepository
            .findById(indexInfoId)
            .orElseThrow(
                () ->
                    new NoSuchElementException("IndexInfo does not exist for id: " + indexInfoId));

    Optional<IndexData> latestIndexDataGet = dashboardRepository.findTopByIndexInfoIdOrderByBaseDateDesc(indexInfoId);

    if (latestIndexDataGet.isEmpty()) {
      return null;
    }

    IndexData latestIndexData = latestIndexDataGet.get();

    // endDate 계산
    LocalDate endDate = latestIndexData.getBaseDate();
    // chartPeriodType에 따른 startDate 계산
    LocalDate startDate =
        switch (chartPeriodType) {
          case MONTHLY -> endDate.minusMonths(1L);
          case QUARTERLY -> endDate.minusMonths(3L);
          case YEARLY -> endDate.minusYears(1L);
        };

    // startDate 30일전 - endDate의 모든 IndexData을 fetch
    // sliding window를 위한 content buffer
    List<IndexData> indexDataList =
        findRangeIndexData(
            indexInfoId,
            startDate.minusDays(30L), // 한달 전
            endDate);

    List<ChartDataPoint> dataPoints = new ArrayList<>();
    List<ChartDataPoint> ma5DataPoints = new ArrayList<>();
    List<ChartDataPoint> ma20DataPoints = new ArrayList<>();

    double ma5Sum = 0.0;
    double ma20Sum = 0.0;
    final int ma5Window = 5;
    final int ma20Window = 20;

    for (int i = 0; i < indexDataList.size(); i++) {

      IndexData currentData = indexDataList.get(i);
      LocalDate currentDate = currentData.getBaseDate();
      double currentClosingPrice = currentData.getClosingPrice();

      ma5Sum += currentClosingPrice;
      ma20Sum += currentClosingPrice;

      // window가 slide하면, window 밖에 있는 요소를 빼기
      // 그 전 요소만 빼기
      if (i >= ma5Window) {
        ma5Sum -= indexDataList.get(i - ma5Window).getClosingPrice();
      }
      if (i >= ma20Window) {
        ma20Sum -= indexDataList.get(i - ma20Window).getClosingPrice();
      }

      // startDate 이후에만 content point 더함
      if (currentDate.isEqual(startDate) || currentDate.isAfter(startDate)) {
        dataPoints.add(new ChartDataPoint(currentDate, currentData.getClosingPrice()));

        // window 사이즈 이후
        // 0,1,2,3,4 -> 5개니까 4부터 시작
        if (i >= ma5Window - 1) {
          ma5DataPoints.add(new ChartDataPoint(currentDate, ma5Sum / ma5Window));
        } else {
          // 앞에 빈 DataPoint 필요
          ma5DataPoints.add(new ChartDataPoint(currentDate, null));
        }

        // For MA20, do the same
        if (i >= ma20Window - 1) {
          ma20DataPoints.add(new ChartDataPoint(currentDate, ma20Sum / ma20Window));
        } else {
          ma20DataPoints.add(new ChartDataPoint(currentDate, null));
        }
      }
    }

    return new IndexChartDto(
        indexInfoId,
        indexInfo.getIndexClassification(),
        indexInfo.getIndexName(),
        chartPeriodType,
        dataPoints,
        ma5DataPoints,
        ma20DataPoints);
  }

  /** 지수 성과 */
  @Override
  public List<RankedIndexPerformanceDto> getPerformanceRank(
      Long indexInfoId, PeriodType periodType, int limit) {

    // 모든 IndexInfo 가져오기
    List<IndexInfo> allIndexInfos = indexInfoRepository.findAll();
    // 위의 리스트 토대로 id 리스트 생성
    List<Long> indexInfoIdList = allIndexInfos.stream()
        .map(IndexInfo::getId)
        .toList();

    LocalDate currentDate = LocalDate.now();
    LocalDate pastDate = calculateMinusDate(periodType);

    // **ROBUST: Get most recent available data (not exact dates)**
    List<IndexData> currentDataList = dashboardRepository
        .findMostRecentByIndexInfoIdsAndMaxDate(indexInfoIdList, currentDate);

    List<IndexData> pastDataList = dashboardRepository
        .findClosestPastByIndexInfoIdsAndTargetDate(
            indexInfoIdList,
            pastDate,
            pastDate.minusDays(30)); // Look back up to 30 days for past data

    // Convert to maps for O(1) lookup
    Map<Long, IndexData> currentDataMap = currentDataList.stream()
        .collect(Collectors.toMap(data -> data.getIndexInfo().getId(), Function.identity()));

    Map<Long, IndexData> pastDataMap = pastDataList.stream()
        .collect(Collectors.toMap(data -> data.getIndexInfo().getId(), Function.identity()));


    // 1) indexInfo당 위의 맵으로 currentData, pastData 구함
    // 2) fluctuationRate 계산
    // 3) performanceDto 생성
    List<PerformanceDto> performanceDtoList = allIndexInfos.stream()
        .map(indexInfo -> {
          IndexData currentData = currentDataMap.get(indexInfo.getId());
          IndexData pastData = pastDataMap.get(indexInfo.getId());

          // Skip if either data point is missing
          if (currentData == null || pastData == null) {
            return null;
          }

          double currentPrice = currentData.getClosingPrice();
          double pastPrice = pastData.getClosingPrice();
          double versus = currentPrice - pastPrice;
          double fluctuationRate = 0.0;

          if (pastPrice != 0) {
            fluctuationRate = versus / pastPrice * 100;
          }

          return new PerformanceDto(
              indexInfo.getId(),
              indexInfo.getIndexClassification(),
              indexInfo.getIndexName(),
              versus,
              fluctuationRate,
              currentPrice,
              pastPrice);
        })
        .filter(Objects::nonNull) // Remove nulls
        .toList();

    // Early return if no valid performance data
    if (performanceDtoList.isEmpty()) {
      return Collections.emptyList();
    }


    // PerformanceDto 리스트를 fluctuationRate로 sort하기
    List<PerformanceDto> sortedPerformanceList =
        performanceDtoList.stream()
            .sorted(Comparator.comparingDouble(PerformanceDto::fluctuationRate).reversed())
            .toList();

    // Sorted된 PerformanceDto를 랭킹과 합친 RankedIndexPerformanceDto로 만들기
    // Ranking : PerformanceDto
    List<RankedIndexPerformanceDto> allRankedDtos =
        IntStream.range(0, sortedPerformanceList.size())
            .mapToObj(
                i ->
                    new RankedIndexPerformanceDto(
                        sortedPerformanceList.get(i), // PerformanceDto
                        i + 1 // ranking
                        ))
            .toList();


    // ============================ indexInfoId가 존재할 경우 (단일 조회) =======================
    if (indexInfoId != null) {
      return allRankedDtos.stream()
          .filter(dto -> Objects.equals(dto.performance().indexInfoId(), indexInfoId))
          .toList();
    }

    // ============================ indexInfoId가 존재하지 않을 경우 (리스트 조회) =======================

    // limit에 맞춰서 allRankedDtos 자르기
    int trueLimit = Math.min(limit, allRankedDtos.size());
    return new ArrayList<>(allRankedDtos.subList(0, trueLimit));


  }

  // ==================================== private 메서드 ====================================

  // 즐겨찾기 성과 메서드
  private LocalDate calculateMinusDate(PeriodType periodType) {
    LocalDate current = LocalDate.now();
    return switch (periodType) {
      case DAILY -> current.minusDays(1);
      case WEEKLY -> current.minusDays(7);
      case MONTHLY -> current.minusMonths(1);
      case QUARTERLY -> current.minusMonths(3L);
      case YEARLY -> current.minusYears(1L);
      default -> throw new IllegalStateException("Unexpected value: " + periodType);
    };
  }


  // 차트 메서드
  private List<IndexData> findRangeIndexData(
      long indexInfoId, LocalDate startDate, LocalDate endDate) {
    return dashboardRepository.findByIndexInfoIdAndBaseDateBetweenOrderByBaseDateAsc(
        indexInfoId, startDate, endDate);
  }
}
