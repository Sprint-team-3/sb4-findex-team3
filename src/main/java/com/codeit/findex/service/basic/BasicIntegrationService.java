package com.codeit.findex.service.basic;

import com.codeit.findex.dto.IndexDataDto;
import com.codeit.findex.dto.IndexInfoDto;
import com.codeit.findex.dto.dashboard.OpenApiResponseDto;
import com.codeit.findex.dto.integration.IndexDataSyncRequest;
import com.codeit.findex.dto.integration.SyncJobDto;
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
  private final ExternalApiService externalApiService;
  private final IndexInfoMapper indexInfoMapper;
  private final IndexDataMapper indexDataMapper;

  @Override
  public List<SyncJobDto> integrateIndexInfo(HttpServletRequest request) {
    OpenApiResponseDto openApiDto = externalApiService.fetchStockMarketIndex();
    List<IndexInfoDto> indexInfoDtosInOpenApi = toIndexInfoDtoList(openApiDto);
    String workerIp = request.getRemoteAddr();

    return indexInfoDtosInOpenApi.stream()
        .map(
            dto -> {
              Optional<IndexInfo> existingOpt =
                  indexInfoRepository.findByIndexName(dto.indexName());
              IndexInfo indexInfo;

              if (existingOpt.isPresent()) {
                indexInfo = existingOpt.get();
                indexInfoMapper.updateInfoFromDto(dto, indexInfo);
              } else {
                indexInfo = indexInfoMapper.toIndexInfo(dto);
                indexInfoRepository.save(indexInfo);
              }

              return buildSyncJob(indexInfo, workerIp);
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

                        return buildSyncJob(indexData, workerIp);
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

  private SyncJobDto buildSyncJob(IndexInfo indexInfo, String worker) {
    return new SyncJobDto(
        indexInfo.getId(),
        JobType.INDEX_INFO,
        indexInfo.getId(),
        indexInfo.getBasepointInTime(),
        worker,
        LocalDateTime.now(),
        Result.SUCCESS);
  }

  private SyncJobDto buildSyncJob(IndexData indexData, String worker) {
    return new SyncJobDto(
        indexData.getId(),
        JobType.INDEX_DATA,
        indexData.getIndexInfo().getId(),
        indexData.getBaseDate(),
        worker,
        LocalDateTime.now(),
        Result.SUCCESS);
  }
}
