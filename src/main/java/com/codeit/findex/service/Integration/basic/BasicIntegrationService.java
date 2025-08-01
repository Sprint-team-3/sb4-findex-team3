package com.codeit.findex.service.Integration.basic;

import com.codeit.findex.dto.IndexDataDto;
import com.codeit.findex.dto.IndexInfoDto;
import com.codeit.findex.dto.dashboard.OpenApiResponseDto;
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
import com.codeit.findex.service.Integration.IntegrationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
    String workerIp = request.getRemoteAddr();

    return indexInfoDtosInOpenApi.stream()
        .map(
            dto -> {
              Optional<IndexInfo> existingOpt =
                  indexInfoRepository.findByIndexClassificationAndIndexName(
                      dto.indexClassification(), dto.indexName());
              IndexInfo indexInfo;

              if (existingOpt.isPresent()) {
                indexInfo = existingOpt.get();
                indexInfoMapper.updateInfoFromDto(dto, indexInfo);
              } else {
                indexInfo = indexInfoMapper.toIndexInfo(dto);
                indexInfoRepository.save(indexInfo);
              }

              Integration integration = saveIntegrationInfoLog(indexInfo, workerIp);

              return integrationMapper.toSyncJobDto(integration.getId(), indexInfo, workerIp);
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

                        Integration integration =
                            saveIntegrationDataLog(indexInfo, indexData, workerIp);

                        return integrationMapper.toSyncJobDto(
                            integration.getId(), indexData, workerIp);
                      });
            })
        .toList();
  }

  @Override
  public CursorPageResponseSyncJobDto integrateCursorPage(
      JobType jobType,
      IndexDataSyncRequest indexDataSyncRequest,
      String worker,
      LocalDateTime jobTimeFrom,
      LocalDateTime jobTimeTo,
      Result status,
      long idAfter,
      String cursor,
      String sortField,
      String sortDirection,
      int size) {
    long totalElements =
        integrationRepository.countByConditions(
            jobType,
            indexDataSyncRequest.indexInfoIds(),
            indexDataSyncRequest.baseDateFrom(),
            indexDataSyncRequest.baseDateTo(),
            worker,
            status,
            jobTimeFrom,
            jobTimeTo);

    List<Integration> integrations =
        integrationRepository.findByConditionsWithCursor(
            jobType,
            indexDataSyncRequest.indexInfoIds(),
            indexDataSyncRequest.baseDateFrom(),
            indexDataSyncRequest.baseDateTo(),
            worker,
            status,
            jobTimeFrom,
            jobTimeTo,
            idAfter,
            sortField,
            sortDirection,
            Pageable.ofSize(size + 1));

    boolean hasNext = integrations.size() > size;
    if (hasNext) {
      integrations = integrations.subList(0, size);
    }

    List<SyncJobDto> syncJobDtos = integrationMapper.toSyncJobDtos(integrations);

    Long nextIdAfter =
        !integrations.isEmpty() ? integrations.get(integrations.size() - 1).getId() : null;
    String nextCursor = nextIdAfter != null ? nextIdAfter.toString() : null;

    return new CursorPageResponseSyncJobDto(
        syncJobDtos, nextCursor, nextIdAfter, size, totalElements, hasNext);
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
    integration.setBaseDate(indexInfo.getBasepointInTime());
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
}
