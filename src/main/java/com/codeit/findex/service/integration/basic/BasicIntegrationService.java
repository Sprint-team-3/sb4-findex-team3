package com.codeit.findex.service.integration.basic;

import com.codeit.findex.dto.openapi.OpenApiResponseDto;
import com.codeit.findex.dto.indexData.response.IndexDataDto;
import com.codeit.findex.dto.indexInfo.response.IndexInfoDto;
import com.codeit.findex.dto.integration.CursorPageResponseSyncJobDto;
import com.codeit.findex.dto.integration.IndexDataSyncRequest;
import com.codeit.findex.dto.integration.SyncJobDto;
import com.codeit.findex.entity.IndexData;
import com.codeit.findex.entity.IndexInfo;
import com.codeit.findex.entity.Integration;
import com.codeit.findex.entityEnum.JobType;
import com.codeit.findex.entityEnum.Result;
import com.codeit.findex.entityEnum.SourceType;
import com.codeit.findex.mapper.IndexDataMapper;
import com.codeit.findex.mapper.IndexInfoMapper;
import com.codeit.findex.mapper.IntegrationMapper;
import com.codeit.findex.repository.IndexDataRepository;
import com.codeit.findex.repository.IndexInfoRepository;
import com.codeit.findex.repository.IntegrationRepository;
import com.codeit.findex.service.ExternalApiService;
import com.codeit.findex.service.integration.IntegrationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class BasicIntegrationService implements IntegrationService {
  private final IndexInfoRepository indexInfoRepository;
  private final IndexDataRepository indexDataRepository;
  private final IntegrationRepository integrationRepository;
  private final ExternalApiService externalApiService;
  private final IndexInfoMapper indexInfoMapper;
  private final IndexDataMapper indexDataMapper;
  private final IntegrationMapper integrationMapper;

  @Override
  @Transactional
  public List<SyncJobDto> integrateIndexInfo(HttpServletRequest request) {
    OpenApiResponseDto openApiDto = externalApiService.fetchStockMarketIndex();
    List<IndexInfoDto> indexInfoDtosInOpenApi = toIndexInfoDtoList(openApiDto);
    String workerIp = extractClientIp(request);

    // 1. 필요한 IndexInfo를 모두 조회해서 Map으로 캐싱
    List<String> keys = indexInfoDtosInOpenApi.stream()
        .map(dto -> dto.getIndexClassification() + "::" + dto.getIndexName())
        .distinct()
        .toList();

    List<IndexInfo> existingInfos = indexInfoRepository.findByCompositeKeys(keys); // Custom query 필요
    Map<String, IndexInfo> existingMap = existingInfos.stream()
        .collect(Collectors.toMap(
            i -> i.getIndexClassification() + "::" + i.getIndexName(),
            Function.identity()));

    // 2. 최근 Integration 로그도 미리 조회 (Optional)
    Map<Long, Integration> latestIntegrationMap =
        integrationRepository.findRecentSuccessLogs(JobType.INDEX_INFO, workerIp, LocalDateTime.now().minusMinutes(1))
            .stream()
            .collect(Collectors.toMap(i -> i.getIndexInfo().getId(), Function.identity()));

    // 3. 처리
    return indexInfoDtosInOpenApi.stream()
        .map(dto -> {
          String key = dto.getIndexClassification() + "::" + dto.getIndexName();
          IndexInfo indexInfo;

          if (existingMap.containsKey(key)) {
            indexInfo = existingMap.get(key);
            indexInfoMapper.updateInfoFromDto(dto, indexInfo); // 변경 감지 활용
          } else {
            indexInfo = indexInfoMapper.toIndexInfo(dto);
            indexInfoRepository.save(indexInfo);
          }

          Integration integration = latestIntegrationMap.get(indexInfo.getId());
          if (integration == null) {
            integration = saveIntegrationInfoLog(indexInfo, workerIp);
          }

          return integrationMapper.toSyncJobDto(integration);
        })
        .toList();
  }


  @Override
  @Transactional
  public List<SyncJobDto> integrateIndexData(IndexDataSyncRequest indexDataSyncRequest, HttpServletRequest request) {

    OpenApiResponseDto openApiDto = externalApiService.fetchStockMarketIndex();
    List<IndexInfo> indexInfos = indexInfoRepository.findAllByIdIn(indexDataSyncRequest.indexInfoIds());
    String workerIp = extractClientIp(request);
    List<SyncJobDto> result = new ArrayList<>();

    for (IndexInfo indexInfo : indexInfos) {

      List<IndexDataDto> indexDataDtos = toIndexDataDtoList(openApiDto, indexInfo, indexDataSyncRequest);

      // ✅ 1. baseDate 기준으로 기존 데이터 조회
      List<LocalDate> baseDates = indexDataDtos.stream()
          .map(IndexDataDto::baseDate)
          .toList();

      List<IndexData> existingDataList =
          indexDataRepository.findAllByIndexInfoAndBaseDateIn(indexInfo, baseDates);

      Map<LocalDate, IndexData> existingDataMap = existingDataList.stream()
          .collect(Collectors.toMap(IndexData::getBaseDate, Function.identity()));

      // ✅ 2. 최근 Integration 로그 조회 (1분 이내)
      List<Integration> recentIntegrations =
          integrationRepository.findRecentLogs(indexInfo.getId(), JobType.INDEX_DATA, workerIp, LocalDateTime.now().minusMinutes(1));

      Map<LocalDate, Integration> integrationMap = recentIntegrations.stream()
          .collect(Collectors.toMap(
              i -> i.getIndexData().getBaseDate(),
              Function.identity()
          ));

      // ✅ 3. 처리 루프
      for (IndexDataDto dto : indexDataDtos) {
        LocalDate baseDate = dto.baseDate();
        IndexData indexData;

        if (existingDataMap.containsKey(baseDate)) {
          indexData = existingDataMap.get(baseDate);
          indexDataMapper.updateDataFromDto(dto, indexData); // 변경 감지
        } else {
          indexData = indexDataMapper.toIndexData(dto);
          indexData.setIndexInfo(indexInfo);
          indexDataRepository.save(indexData);
        }

        // 중복 로그 재사용
        Integration integration = integrationMap.get(baseDate);
        if (integration == null) {
          integration = saveIntegrationDataLog(indexInfo, indexData, workerIp);
        }

        result.add(integrationMapper.toSyncJobDto(integration));
      }
    }

    return result;
  }



  @Override
  public CursorPageResponseSyncJobDto integrateCursorPage(
      JobType jobType,
      Long indexInfoId,
      LocalDate baseDateFrom,
      LocalDate baseDateTo,
      String worker,
      LocalDateTime jobTimeFrom,
      LocalDateTime jobTimeTo,
      Result status,
      Long idAfter,
      String cursor,
      String sortField,
      String sortDirection,
      int size) {
    PageRequest pageRequest = PageRequest.of(0, size);

    // 커서 시간 파싱
    LocalDateTime cursorDateTime = null;
    if (cursor != null && !cursor.isEmpty()) {
      cursorDateTime = LocalDateTime.parse(cursor);
    }

    // 데이터 조회
    Slice<Integration> integrationSlice =
        integrationRepository.searchIntegrations(
            jobType,
            indexInfoId,
            baseDateFrom,
            baseDateTo,
            worker,
            jobTimeFrom,
            jobTimeTo,
            status,
            cursorDateTime,
            sortField,
            sortDirection,
            pageRequest);

    // 전체 갯수 조회
    long totalElements =
        integrationRepository.countIntegrations(
            jobType, indexInfoId, baseDateFrom, baseDateTo, worker, jobTimeFrom, jobTimeTo, status);

    String nextCursor = null;
    Long nextIdAfter = null;

    List<Integration> content = integrationSlice.getContent();
    if (content != null && !content.isEmpty()) {
      int lastIndex = content.size() - 1;

      if (integrationSlice.hasNext()) {
        Integration lastIntegration = content.get(lastIndex);
        nextCursor = lastIntegration.getJobTime().toString();
        nextIdAfter = lastIntegration.getId();
      }
    }

    List<SyncJobDto> syncJobDtos =
        integrationSlice.getContent().stream().map(integrationMapper::toSyncJobDto).toList();

    // 응답 생성
    return CursorPageResponseSyncJobDto.of(
        syncJobDtos,
        nextCursor, // 이번 페이지 마지막 아이템의 jobTime 커서 (다음 페이지 요청 때 사용)
        nextIdAfter, // 이번 페이지 마지막 아이템의 id (동일 jobTime 내 중복 방지용)
        size,
        totalElements,
        integrationSlice.hasNext());
  }

  private List<IndexInfoDto> toIndexInfoDtoList(OpenApiResponseDto responseDto) {
    return responseDto.response().body().items().item().stream()
        .map(
            indexItemDto ->
                new IndexInfoDto(
                    null,
                    indexItemDto.idxCsf(),
                    indexItemDto.idxNm(),
                    indexItemDto.epyItmsCnt(),
                    LocalDate.parse(
                        indexItemDto.basPntm(), DateTimeFormatter.ofPattern("yyyyMMdd")),
                    indexItemDto.basIdx(),
                    SourceType.OPEN_API,
                    false))
        .toList();
  }

  private List<IndexDataDto> toIndexDataDtoList(
      OpenApiResponseDto responseDto,
      IndexInfo indexInfo,
      IndexDataSyncRequest indexDataSyncRequest) {
    String indexName = indexInfo.getIndexName();
    String indexClassification = indexInfo.getIndexClassification();

    return responseDto.response().body().items().item().stream()
        .filter(item -> indexName.equals(item.idxNm()))
        .filter(item -> indexClassification.equals(item.idxCsf()))
        .filter(
            item -> {
              LocalDate itemDate =
                  LocalDate.parse(item.basDt(), DateTimeFormatter.ofPattern("yyyyMMdd"));
              return !itemDate.isBefore(indexDataSyncRequest.baseDateFrom())
                  && !itemDate.isAfter(indexDataSyncRequest.baseDateTo());
            })
        .map(
            item ->
                new IndexDataDto(
                    null,
                    indexInfo.getId(),
                    LocalDate.parse(item.basDt(), DateTimeFormatter.ofPattern("yyyyMMdd")),
                    SourceType.OPEN_API,
                    item.mkp(),
                    item.clpr(),
                    item.hipr(),
                    item.lopr(),
                    item.vs(),
                    item.fltRt(),
                    item.trqu(),
                    item.trPrc(),
                    item.lstgMrktTotAmt()))
        .toList();
  }

  private Integration saveIntegrationInfoLog(IndexInfo indexInfo, String workerIp) {
    Integration integration = new Integration();
    integration.setIndexInfo(indexInfo);
    integration.setIndexData(null);
    integration.setJobType(JobType.INDEX_INFO);
    integration.setBaseDate(null);
    integration.setWorker(workerIp);
    integration.setJobTime(LocalDateTime.now());
    integration.setResult(Result.SUCCESS);

    return integrationRepository.save(integration);
  }

  private Integration saveIntegrationDataLog(
      IndexInfo indexInfo, IndexData indexData, String workerIp) {
    Integration integration = new Integration();
    integration.setIndexInfo(indexInfo);
    integration.setIndexData(indexData);
    integration.setJobType(JobType.INDEX_DATA);
    integration.setBaseDate(indexData.getBaseDate());
    integration.setWorker(workerIp);
    integration.setJobTime(LocalDateTime.now());
    integration.setResult(Result.SUCCESS);

    return integrationRepository.save(integration);
  }

  private String extractClientIp(HttpServletRequest request) {
    String xForwardedFor = request.getHeader("X-Forwarded-For");
    if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
      // 다중 프록시일 경우 제일 앞의 IP가 실제 클라이언트 IP
      return xForwardedFor.split(",")[0].trim();
    }
    return request.getRemoteAddr(); // fallback
  }

}
