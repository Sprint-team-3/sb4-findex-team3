/*
<<<<<<<< HEAD:src/main/java/com/codeit/findex/service/Integration/basic/BasicIntegrationService.java
package com.codeit.findex.service.Integration.basic;

import com.codeit.findex.dto.indexData.response.IndexDataDto;
import com.codeit.findex.dto.indexInfo.response.IndexInfoDto;
========
package com.codeit.findex.service.basic;

import com.codeit.findex.dto.indexData.response.IndexDataDto;
import com.codeit.findex.dto.IndexInfoDto;
>>>>>>>> dev:src/main/java/com/codeit/findex/service/basic/BasicIntegrationService.java
import com.codeit.findex.dto.dashboard.OpenApiResponseDto;
import com.codeit.findex.dto.integration.IndexDataSyncRequest;
import com.codeit.findex.dto.integration.SyncJobDto;
import com.codeit.findex.entity.IndexData;
import com.codeit.findex.entity.IndexInfo;
import com.codeit.findex.entity.Integration;
import com.codeit.findex.entityEnum.JobType;
import com.codeit.findex.entityEnum.Result;
import com.codeit.findex.entityEnum.SourceType;
import com.codeit.findex.mapper.IndexDataIntegrationMapper;
import com.codeit.findex.mapper.IndexInfoMapper;
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
  private final IndexDataIntegrationMapper indexDataIntegrationMapper;

  @Override
  public List<SyncJobDto> integrateIndexInfo(HttpServletRequest request) {
    OpenApiResponseDto openApiDto = externalApiService.fetchStockMarketIndex();
    List<IndexInfoDto> indexInfoDtosInOpenApi = toIndexInfoDtoList(openApiDto);
    String workerIp = request.getRemoteAddr();

    LocalDateTime now = LocalDateTime.now();

    return indexInfoDtosInOpenApi.stream()
        .map(
            dto -> {
              Optional<IndexInfo> existingOpt =
                  indexInfoRepository.findByIndexClassificationAndIndexName(
                      dto.indexClassification(), dto.indexName());
              IndexInfo indexInfo = new IndexInfo();

              if (existingOpt.isPresent()) {
                indexInfo = existingOpt.get();
                indexInfoMapper.updateInfoFromDto(dto, indexInfo);
              } else {
                indexInfo = indexInfoMapper.toIndexInfo(dto);
                indexInfoRepository.save(indexInfo);
              }

              Integration integration = saveIntegrationInfoLog(indexInfo, now, workerIp);

              return buildSyncJob(integration.getId(), indexInfo, workerIp);
            })
        .toList();
  }

  @Override
  public List<SyncJobDto> integrateIndexData(
      IndexDataSyncRequest indexDataSyncRequest, HttpServletRequest request) {

    OpenApiResponseDto openApiDto = externalApiService.fetchStockMarketIndex();
    List<IndexInfo> indexInfos =
        indexInfoRepository.findAllById(indexDataSyncRequest.indexInfoIds());
    String workerIp = request.getRemoteAddr();

    LocalDate baseDateFrom = indexDataSyncRequest.baseDateFrom();
    LocalDate baseDateTo = indexDataSyncRequest.baseDateTo();
    LocalDateTime now = LocalDateTime.now();

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
                          indexDataIntegrationMapper.updateDataFromDto(dto, indexData);
                        } else {
                          indexData = indexDataIntegrationMapper.toIndexData(dto);
                          indexData.setIndexInfo(indexInfo);
                          indexDataRepository.save(indexData);
                        }

                        Integration integration =
                            saveIntegrationDataLog(
                                indexInfo, indexData, baseDateFrom, baseDateTo, now, workerIp);

                        return buildSyncJob(integration.getId(), indexData, workerIp);
                      });
            })
        .toList();
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

  private SyncJobDto buildSyncJob(long integrationId, IndexInfo indexInfo, String worker) {
    return new SyncJobDto(
        integrationId,
        JobType.INDEX_INFO,
        indexInfo.getId(),
        indexInfo.getBasepointInTime(),
        worker,
        LocalDateTime.now(),
        Result.SUCCESS);
  }

  private SyncJobDto buildSyncJob(long integrationId, IndexData indexData, String worker) {
    return new SyncJobDto(
        integrationId,
        JobType.INDEX_DATA,
        indexData.getIndexInfo().getId(),
        indexData.getBaseDate(),
        worker,
        LocalDateTime.now(),
        Result.SUCCESS);
  }

  private Integration saveIntegrationInfoLog(
      IndexInfo indexInfo, LocalDateTime jobStartTime, String workerIp) {
    if (indexInfo == null) {
      throw new IllegalArgumentException("indexInfo must not be null");
    }
    Integration integration = new Integration();
    integration.setIndexInfo(indexInfo);
    integration.setIndexData(null);
    integration.setJobType(JobType.INDEX_INFO);
    integration.setBaseDateFrom(null);
    integration.setBaseDateTo(null);
    integration.setWorker(workerIp);
    integration.setJobTimeFrom(jobStartTime);
    integration.setJobTimeTo(LocalDateTime.now());
    integration.setResult(Result.SUCCESS);

    return integrationRepository.save(integration);
  }

  private Integration saveIntegrationDataLog(
      IndexInfo indexInfo,
      IndexData indexData,
      LocalDate baseDateFrom,
      LocalDate baseDateTo,
      LocalDateTime jobStartTime,
      String workerIp) {
    if (indexInfo == null) {
      throw new IllegalArgumentException("indexInfo must not be null");
    }
    Integration integration = new Integration();
    integration.setIndexInfo(indexInfo);
    integration.setIndexData(indexData);
    integration.setJobType(JobType.INDEX_DATA);
    integration.setBaseDateFrom(baseDateFrom);
    integration.setBaseDateTo(baseDateTo);
    integration.setWorker(workerIp);
    integration.setJobTimeFrom(jobStartTime);
    integration.setJobTimeTo(LocalDateTime.now());
    integration.setResult(Result.SUCCESS);

    return integrationRepository.save(integration);
  }
}
*/
