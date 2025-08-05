package com.codeit.findex.service.basic;

import com.codeit.findex.dto.dashboard.OpenApiResponseDto;
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
import com.codeit.findex.service.IntegrationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
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
  public List<SyncJobDto> integrateIndexInfo(HttpServletRequest request) {
    OpenApiResponseDto openApiDto = externalApiService.fetchStockMarketIndex();
    List<IndexInfoDto> indexInfoDtosInOpenApi = toIndexInfoDtoList(openApiDto);
    String workerIp = extractClientIp(request);

    return indexInfoDtosInOpenApi.stream()
        .map(
            dto -> {
              Optional<IndexInfo> existingOpt =
                  indexInfoRepository.findByIndexClassificationAndIndexName(
                      dto.getIndexClassification(), dto.getIndexName());
              IndexInfo indexInfo;

              if (existingOpt.isPresent()) {
                indexInfo = existingOpt.get();
                indexInfoMapper.updateInfoFromDto(dto, indexInfo);
              } else {
                indexInfo = indexInfoMapper.toIndexInfo(dto);
                indexInfoRepository.save(indexInfo);
              }

              // 중복 방지: 최근 동일한 성공 로그가 있으면 재사용
              Integration integration;
              Optional<Integration> latestOpt =
                  integrationRepository.findTopByIndexInfoAndJobTypeAndWorkerOrderByJobTimeDesc(
                      indexInfo, JobType.INDEX_INFO, workerIp);

              if (latestOpt.isPresent()) {
                Integration latest = latestOpt.get();
                boolean isRecentSuccess =
                    latest.getResult() == Result.SUCCESS
                        && latest.getJobTime().isAfter(LocalDateTime.now().minusMinutes(1));
                if (isRecentSuccess) {
                  integration = latest;
                } else {
                  integration = saveIntegrationInfoLog(indexInfo, workerIp);
                }
              } else {
                integration = saveIntegrationInfoLog(indexInfo, workerIp);
              }

              return integrationMapper.toSyncJobDto(integration);
            })
        .toList();
  }

  @Override
  public List<SyncJobDto> integrateIndexData(
      IndexDataSyncRequest indexDataSyncRequest, HttpServletRequest request) {

    OpenApiResponseDto openApiDto = externalApiService.fetchStockMarketIndex();
    List<IndexInfo> indexInfos =
        indexInfoRepository.findAllById(indexDataSyncRequest.indexInfoIds());
    String workerIp = extractClientIp(request);

    return indexInfos.stream()
        .flatMap(
            indexInfo -> {
              List<IndexDataDto> indexDataDtos =
                  toIndexDataDtoList(openApiDto, indexInfo, indexDataSyncRequest);

              return indexDataDtos.stream()
                  .map(
                      dto -> {
                        Optional<IndexData> existingOpt =
                            indexDataRepository.findByIndexInfoIdAndBaseDate(
                                indexInfo.getId(), dto.baseDate());

                        IndexData indexData;
                        if (existingOpt.isPresent()) {
                          indexData = existingOpt.get();
                          indexDataMapper.updateDataFromDto(dto, indexData);
                        } else {
                          indexData = indexDataMapper.toIndexData(dto);
                          indexData.setIndexInfo(indexInfo);
                          indexDataRepository.save(indexData);
                        }

                        // 중복 Integration 로그 방지
                        Integration integration;
                        Optional<Integration> latestOpt =
                            integrationRepository
                                .findTopByIndexInfoAndIndexDataAndJobTypeAndWorkerOrderByJobTimeDesc(
                                    indexInfo, indexData, JobType.INDEX_DATA, workerIp);

                        boolean isRecentSuccess =
                            latestOpt.isPresent()
                                && latestOpt.get().getResult() == Result.SUCCESS
                                && latestOpt
                                    .get()
                                    .getJobTime()
                                    .isAfter(LocalDateTime.now().minusMinutes(1));

                        if (isRecentSuccess) {
                          integration = latestOpt.get();
                        } else {
                          integration = saveIntegrationDataLog(indexInfo, indexData, workerIp);
                        }

                        return integrationMapper.toSyncJobDto(integration);
                      });
            })
        .toList();
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

      if (integrationSlice.hasNext() && lastIndex >= 0) {
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

    return responseDto.response().body().items().item().stream()
        .filter(item -> indexName.equals(item.idxNm()))
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
                    null,
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
