package com.codeit.findex.service.basic;

import com.codeit.findex.dto.IndexDataDto;
import com.codeit.findex.dto.indexInfo.response.IndexInfoDto
import com.codeit.findex.dto.IndexDataSyncRequest;
import com.codeit.findex.dto.OpenApiResponseDto;
import com.codeit.findex.dto.SyncJobDto;
import com.codeit.findex.entity.IndexData;
import com.codeit.findex.entity.IndexInfo;
import com.codeit.findex.entityEnum.JobType;
import com.codeit.findex.entityEnum.Result;
import com.codeit.findex.entityEnum.SourceType;
import com.codeit.findex.mapper.IndexDataMapper;
import com.codeit.findex.mapper.IndexInfoMapper;
import com.codeit.findex.repository.IndexDataRepository;
import com.codeit.findex.repository.IndexInfoRepository;
import com.codeit.findex.service.ExternalApiService;
import com.codeit.findex.service.IntegrationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class BasicIntegrationService implements IntegrationService {
  private final IndexInfoRepository indexInfoRepository;
  private final IndexDataRepository indexDataRepository;
  private final ExternalApiService externalApiService;
  private final IndexInfoMapper indexInfoMapper;
  private final IndexDataMapper indexDataMapper;

  @Override
  public List<SyncJobDto> integrateIndexInfo(HttpServletRequest request) {
    OpenApiResponseDto responseDto = externalApiService.fetchStockMarketIndex();
    List<IndexInfoDto> indexInfoDtos = toIndexInfoDtoList(responseDto);

    List<SyncJobDto> syncJobDtos = new ArrayList<>();

    String workerIp = request.getRemoteAddr();

    for (IndexInfoDto indexInfoDto : indexInfoDtos) {
      Optional<IndexInfo> existingOpt =
          indexInfoRepository.findByIndexName(indexInfoDto.indexName());

      if (existingOpt.isPresent()) {
        IndexInfo indexInfo = existingOpt.get();
        indexInfoMapper.updateInfoFromDto(indexInfoDto, indexInfo);
        syncJobDtos.add(buildSyncJob(indexInfo, workerIp));
      } else {
        IndexInfo indexInfo = indexInfoMapper.toIndexInfo(indexInfoDto);
        indexInfoRepository.save(indexInfo);
        syncJobDtos.add(buildSyncJob(indexInfo, workerIp));
      }
    }
    return syncJobDtos;
  }

  @Override
  public List<SyncJobDto> integrateIndexData(
      IndexDataSyncRequest indexDataSyncRequest, HttpServletRequest request) {

    OpenApiResponseDto responseDto = externalApiService.fetchStockMarketIndex();

    List<IndexDataDto> indexDataDtos =
        toIndexDataList(responseDto, indexDataSyncRequest.indexInfolds());

    List<SyncJobDto> syncJobDtos = new ArrayList<>();

    String workerIp = request.getRemoteAddr();

    for (IndexDataDto indexDataDto : indexDataDtos) {
      Optional<IndexData> existingOpt =
          indexDataRepository.findByIndexInfoId(indexDataDto.indexInfoId());
      if (existingOpt.isPresent()) {
        IndexData indexData = existingOpt.get();
        indexDataMapper.updateDataFromDto(indexDataDto, indexData);
        syncJobDtos.add(buildSyncJob(indexData, workerIp));
      } else {
        IndexData indexData = indexDataMapper.toIndexData(indexDataDto);
        indexDataRepository.save(indexData);
        syncJobDtos.add(buildSyncJob(indexData, workerIp));
      }
    }
    return syncJobDtos;
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

  private List<IndexDataDto> toIndexDataList(
      OpenApiResponseDto responseDto, List<UUID> indexInfoIds) {
    Set<String> targetIndexNames =
        indexInfoRepository.findAllById(indexInfoIds).stream()
            .map(IndexInfo::getIndexName)
            .collect(Collectors.toSet());

    return responseDto.response().body().items().item().stream()
        .filter(item -> targetIndexNames.contains(item.idxNm()))
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

  private SyncJobDto buildSyncJob(IndexInfo indexInfo, String worker) {
    return new SyncJobDto(
        indexInfo.getId(),
        JobType.INDEX_INFO,
        null,
        indexInfo.getBasepointInTime(),
        worker,
        Instant.now(),
        Result.SUCCESS);
  }

  private SyncJobDto buildSyncJob(IndexData indexData, String worker) {
    return new SyncJobDto(
        ,
        JobType.INDEX_DATA,
        indexData.getId(),
        indexData.getBaseDate(),
        worker,
        Instant.now(),
        Result.SUCCESS);
  }
}
