package com.codeit.findex.service.dashboard;

import com.codeit.findex.dto.IndexInfoDto;
import com.codeit.findex.dto.dashboard.ChartDataPoint;
import com.codeit.findex.dto.dashboard.ChartPeriodType;
import com.codeit.findex.dto.dashboard.IndexChartDto;
import com.codeit.findex.dto.dashboard.PerformanceDto;
import com.codeit.findex.dto.dashboard.PeriodType;
import com.codeit.findex.dto.dashboard.RankedIndexPerformanceDto;
import com.codeit.findex.entity.IndexData;
import com.codeit.findex.entity.IndexInfo;
import com.codeit.findex.entityEnum.SourceType;
import com.codeit.findex.repository.DashboardRepository;
import com.codeit.findex.repository.IndexInfoRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicDashboardService implements DashboardService {

  private final IndexInfoRepository indexInfoRepository;
  private final DashboardRepository dashboardRepository;

  // ==================================== 즐겨찾기 지수 현황 요약 ====================================

  @Override
  public List<PerformanceDto> getFavPerformanceDto(PeriodType periodType) {

    // 즐겨 찾기한 IndexData리스트 가져오기
//    List<IndexInfo> favoriteInfos = indexInfoRepository.getFavorites();
    List<IndexInfo> favoriteInfos = null;

    // 위의 리스트 토대로 id 리스트 생성
    List<Long> indexInfoIdList = favoriteInfos.stream()
        .map(IndexInfo::getId)
        .toList();

    // id 리스트에 있는 IndexInfo당 가장 최신 IndexData 가져오기
    // key: indexInfoId, value: IndexData
    List<IndexData> recentIndexDataList = dashboardRepository.findRecentByIndexInfoIds(indexInfoIdList);
    Map<Long, IndexData> recentIndexDataMap = recentIndexDataList.stream()
        .collect(Collectors.toMap(i -> i.getIndexInfo().getId(), i -> i));

    List<PerformanceDto> performanceDtoList = new ArrayList<>();
    for (IndexInfo i : favoriteInfos) {
      long indexInfoId = i.getId();

      IndexData current = recentIndexDataMap.get(indexInfoId);
      LocalDate currentDate = current.getBaseDate();

      Optional<IndexData> comparisonData =
          switch (periodType) {
            case DAILY -> findPastIndexData(indexInfoId, currentDate.minusDays(1));
            case WEEKLY -> findPastIndexData(indexInfoId, currentDate.minusDays(7));
            case MONTHLY -> findPastIndexData(indexInfoId, currentDate.minusMonths(1));
          };

      if (comparisonData.isEmpty()) {
        continue;
      }

      double currentPrice = current.getClosingPrice(); // 증가
      double beforePrice = comparisonData.get().getClosingPrice();
      double versus = currentPrice - beforePrice;
      double fluctuationRate = versus / beforePrice * 100;

      performanceDtoList.add(new PerformanceDto(
          indexInfoId,
          i.getIndexClassification(),
          i.getIndexName(),
          versus,
          fluctuationRate,
          currentPrice,
          beforePrice
        )
      );

    }
    return performanceDtoList;
  }

  // ==================================== 차트 ====================================
  @Override
  public IndexChartDto getChartData(long indexInfoId, ChartPeriodType chartPeriodType) {

    IndexInfo indexInfoDto =
        indexInfoRepository.findById(indexInfoId)
            .orElseThrow(() -> new NoSuchElementException("IndexData does not exist"));

    // 가장 최신 IndexData fetch
    IndexData latestIndexData =
        findRecentIndexData(indexInfoId)
            .orElseThrow(() -> new NoSuchElementException("IndexData does not exist"));

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
    // sliding window를 위한 data buffer
    List<IndexData> indexDataList =
        findRangeIndexData(
            indexInfoId,
            startDate.minusDays(30L), // 한달 전
            endDate);

    List<ChartDataPoint> dataPoints = new ArrayList<>();
    List<ChartDataPoint> ma5DataPoints = new ArrayList<>();
    List<ChartDataPoint> ma20DataPoints = new ArrayList<>();

    IntStream.range(0, indexDataList.size())
        .filter(i -> !indexDataList.get(i).getBaseDate().isBefore(startDate))
        .forEach(
            i -> {
              IndexData currentData = indexDataList.get(i);
              LocalDate currentDate = currentData.getBaseDate();
              dataPoints.add(new ChartDataPoint(currentDate, currentData.getClosingPrice()));

              // 5일 이동평균선
              if (i >= 4) {
                double closingPricetotal = 0;
                for (int j = 0; j < 5; j++) {
                  closingPricetotal += indexDataList.get(i - j).getClosingPrice();
                }
                closingPricetotal /= 5;
                ma5DataPoints.add(
                    new ChartDataPoint(indexDataList.get(i).getBaseDate(), closingPricetotal));
              }

              // 20일 이동평균선
              if (i >= 19) {
                double closingPricetotal = 0;
                for (int j = 0; j < 20; j++) {
                  closingPricetotal += indexDataList.get(i - j).getClosingPrice();
                }
                closingPricetotal /= 20;
                ma20DataPoints.add(
                    new ChartDataPoint(indexDataList.get(i).getBaseDate(), closingPricetotal));
              }
            });

    return new IndexChartDto(
        indexInfoId,
        indexInfoDto.getIndexClassification(),
        indexInfoDto.getIndexName(),
        chartPeriodType,
        dataPoints,
        ma5DataPoints,
        ma20DataPoints
    );
  }

  // ==================================== 지수 성과 분석 랭킹 ====================================
  @Override
  public List<RankedIndexPerformanceDto> getPerformanceRank(long indexInfoId, PeriodType periodType, int limit) {

    // IndexInfo의 가장 최신 IndexData fetch
    IndexData indexData = findRecentIndexData(indexInfoId)
        .orElseThrow(() -> new NoSuchElementException("IndexData not found"));


    LocalDate currentDate = indexData.getBaseDate();
    LocalDate pastDate =
        switch (periodType) {
          case DAILY -> currentDate.minusDays(1);
          case WEEKLY -> currentDate.minusDays(7);
          case MONTHLY -> currentDate.minusMonths(1);
        };

    // 모든 IndexInfo 조회 (1 query)
    List<IndexInfo> allIndexInfos = indexInfoRepository.findAll();

    // 가장 최근 모든 IndexData 리스트 조회 (1 query)
    // map 구현: key - long IndexInfoId, value - IndexData indexData
    List<IndexData> recentIndexDataList = dashboardRepository.findAllRecentIndexData();
    Map<Long, IndexData> recentDataMap = recentIndexDataList.stream()
        .collect(Collectors.toMap(i -> i.getIndexInfo().getId(), i -> i));

    // 특정 날짜 이후 가장 최근 모든 IndexData 리스트 조회 (1 query)
    // map 구현: key - long IndexInfoId, value - IndexData indexData
    List<IndexData> pastIndexDataList = dashboardRepository.findAllPastIndexData(pastDate);
    Map<Long, IndexData> pastDataMap = pastIndexDataList.stream()
        .collect(Collectors.toMap(i -> i.getIndexInfo().getId(), i -> i));

    // 1) indexInfo당 recentIndexData, pastIndexData를 구함
    // 2) fluctuationRate 계산
    // 3) performanceDto 생성
    List<PerformanceDto> performanceDtoList = new ArrayList<>();
    allIndexInfos
        .forEach(
            i -> {
              IndexData recentIndexData = recentDataMap.get(i.getId());
              IndexData pastIndexData = pastDataMap.get(i.getId());

              // NPE 피하기
              if (recentIndexData == null || pastIndexData == null) {
                return;
              }

              // 증가
              double recentClosingPrice = recentIndexData.getClosingPrice();
              double pastClosingPrice = pastIndexData.getClosingPrice();

              // 계산
              double versus = recentClosingPrice - pastClosingPrice;
              double fluctuationRate = versus / pastClosingPrice * 100;

              PerformanceDto performanceDto =  new PerformanceDto(
                  i.getId(),
                  i.getIndexClassification(),
                  i.getIndexName(),
                  versus,
                  fluctuationRate,
                  recentClosingPrice,
                  pastClosingPrice
              );

              performanceDtoList.add(performanceDto);

            }
        );

    // PerformanceDto 리스트를 fluctuationRate로 sort하기
    List<PerformanceDto> sortedPerformanceDtoList = performanceDtoList.stream()
        .sorted(Comparator.comparing(PerformanceDto::fluctuationRate).reversed())
        .toList();

    // Sorted된 PerformanceDto를 랭킹과 합친 RankedIndexPerformanceDto로 만들기
    // Ranking : PerformanceDto
    List<RankedIndexPerformanceDto> allRankedDtos = IntStream.range(0, sortedPerformanceDtoList.size())
        .mapToObj(i -> new RankedIndexPerformanceDto(
            sortedPerformanceDtoList.get(i), // PerformanceDto
            i + 1  // ranking
      )
    ).toList();

    // limit에 맞춰서 allRankedDtos 자르기
    int trueLimit = Math.min(limit, sortedPerformanceDtoList.size());
    List<RankedIndexPerformanceDto> finalResultList = new ArrayList<>(
        allRankedDtos.subList(0, trueLimit));

    // 유저가 보낸 indexInfoId가 리스트에 있는지 확인
    boolean isUserIndexInFinalList = finalResultList.stream()
        .anyMatch(dto -> dto.performance().indexInfoId() == indexInfoId);

    // 리스트에 없으면 추가하기
    if (!isUserIndexInFinalList) {
      allRankedDtos.stream()
          .filter(dto -> dto.performance().indexInfoId() == indexInfoId)
          .findFirst() // Optional<RankedIndexPerformanceDto>
          .ifPresent(finalResultList::add); // 존재하면 finalResultList에 추가
    }

    return finalResultList;
  }


  // ==================================== private 메서드 ====================================

  // 즐겨찾기 성과 메서드
  private Optional<IndexData> findRecentIndexData(long indexInfoId) {
    return dashboardRepository.findTopByIndexInfoIdOrderByBaseDateDesc(indexInfoId);
  }

  private Optional<IndexData> findPastIndexData(long indexInfoId, LocalDate localDate) {
    return dashboardRepository.findTopByIndexInfoIdAndBaseDateLessThanEqualOrderByBaseDateDesc(
        indexInfoId, localDate);
  }

  // 차트 메서드
  private List<IndexData> findRangeIndexData(
      long indexInfoId, LocalDate startDate, LocalDate endDate) {
    return dashboardRepository.findByIndexInfoIdAndBaseDateBetweenOrderByBaseDateAsc(
        indexInfoId, startDate, endDate);
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

}
