/*
package com.codeit.findex.service.Integration.basic;

import com.codeit.findex.dto.indexData.response.IndexDataDto;
import com.codeit.findex.dto.indexInfo.response.IndexInfoDto;
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
import org.springframework.data.domain.Sort;
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

                        return integrationMapper.toSyncJobDto(integration);
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
      Long idAfter,
      String cursor,
      String sortField,
      String sortDirection,
      int size) {

    List<Integration> integrations =
        getIntegrations( // 정렬 완료된 integration 리스트
            jobType,
            worker,
            status,
            indexDataSyncRequest.indexInfoIds(),
            indexDataSyncRequest.baseDateFrom(),
            indexDataSyncRequest.baseDateTo(),
            jobTimeFrom,
            jobTimeTo,
            sortField,
            sortDirection);

    // integrations → CursorPageResponseSyncJobDto 변환 로직 구현
    return toCursorPageResponse(integrations, idAfter, cursor, size);
  }

  private CursorPageResponseSyncJobDto toCursorPageResponse(
      List<Integration> integrations, Long idAfter, String cursor, int size) {
    // 1. 커서(idAfter) 기준 시작 인덱스 찾기
    int startIndex = 0;

    if (idAfter != null) {
      for (int i = 0; i < integrations.size(); i++) {
        if (integrations.get(i).getId() > idAfter) {
          startIndex = i;
          break;
        }
      }
    }

    // 2. 페이지 슬라이스
    int endIndex = Math.min(startIndex + size, integrations.size());
    List<Integration> pageIntegrations = integrations.subList(startIndex, endIndex);

    // 3. DTO 변환
    List<SyncJobDto> dtoList = integrationMapper.toSyncJobDtos(pageIntegrations);

    // 4. nextIdAfter, hasNext 판단
    Long nextId = (endIndex < integrations.size()) ? integrations.get(endIndex - 1).getId() : null;
    boolean hasNext = endIndex < integrations.size();

    // 5. 결과 조립
    return new CursorPageResponseSyncJobDto(
        dtoList,
        nextId != null ? nextId.toString() : null,
        nextId,
        size,
        integrations.size(),
        hasNext);
  }

  private List<Integration> getIntegrations(
      JobType jobType,
      String worker,
      Result result,
      List<Long> indexInfoIds,
      LocalDate baseDateFrom,
      LocalDate baseDateTo,
      LocalDateTime jobTimeFrom,
      LocalDateTime jobTimeTo,
      String sortField,
      String sortDirection
  ) {
    List<JobType> allJobTypes = List.of(JobType.INDEX_INFO, JobType.INDEX_DATA);

    if (indexInfoIds != null && indexInfoIds.isEmpty()) {
      indexInfoIds = null;
    }

    if (sortField == null || sortField.isBlank()) {
      throw new IllegalArgumentException("정렬 기준 필드(sortField)는 반드시 'baseDate' 또는 'jobTime'이어야 합니다.");
    }

    Sort sort = createSort(sortField, sortDirection);

    return switch (sortField) {
      case "baseDate" -> {
        if (baseDateFrom != null && baseDateTo != null) {
          yield integrationRepository.findByBaseDateConditions(
              jobType, allJobTypes, worker, result, indexInfoIds, baseDateFrom, baseDateTo, sort
          );
        } else {
          // baseDate 조건 없으면 시간 조건 없는 전체 조회 (sort 기준으로 정렬)
          yield integrationRepository.findByConditionsNoTimeFilter(
              jobType, allJobTypes, worker, result, indexInfoIds, sort
          );
        }
      }
      case "jobTime" -> {
        if (jobTimeFrom != null && jobTimeTo != null) {
          yield integrationRepository.findByJobTimeConditions(
              jobType, allJobTypes, worker, result, indexInfoIds, jobTimeFrom, jobTimeTo, sort
          );
        } else {
          // jobTime 조건 없으면 시간 조건 없는 전체 조회
          yield integrationRepository.findByConditionsNoTimeFilter(
              jobType, allJobTypes, worker, result, indexInfoIds, sort
          );
        }
      }
      default -> throw new IllegalArgumentException("지원하지 않는 정렬 필드: " + sortField);
    };
  }

  private Sort createSort(String sortField, String sortDirection) {
    if (sortField == null || sortField.isBlank()) {
      sortField = "id"; // 기본 정렬 필드
    }
    Sort.Direction direction = Sort.Direction.ASC; // 기본 오름차순
    if ("DESC".equalsIgnoreCase(sortDirection)) {
      direction = Sort.Direction.DESC;
    }
    return Sort.by(direction, sortField);
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
*/
