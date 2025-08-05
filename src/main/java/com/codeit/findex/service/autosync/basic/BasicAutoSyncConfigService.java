package com.codeit.findex.service.autosync.basic;

import com.codeit.findex.dto.autosync.response.AutoSyncConfigDto;
import com.codeit.findex.dto.autosync.response.CursorPageResponseAutoSyncConfigDto;
import com.codeit.findex.dto.dashboard.OpenApiResponseDto;
import com.codeit.findex.dto.indexData.response.IndexDataDto;
import com.codeit.findex.dto.indexInfo.request.IndexInfoCreateRequest;
import com.codeit.findex.dto.indexInfo.response.IndexInfoDto;
import com.codeit.findex.dto.integration.IndexDataSyncRequest;
import com.codeit.findex.entity.IndexData;
import com.codeit.findex.entity.IndexInfo;
import com.codeit.findex.entityEnum.SourceType;
import com.codeit.findex.mapper.AutoSyncMapper;
import com.codeit.findex.mapper.IndexDataMapper;
import com.codeit.findex.mapper.IndexInfoMapper;
import com.codeit.findex.repository.AutoSyncRepository;
import com.codeit.findex.repository.IndexDataRepository;
import com.codeit.findex.repository.IndexInfoRepository;
import com.codeit.findex.service.ExternalApiService;
import com.codeit.findex.service.autosync.AutoSyncConfigService;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BasicAutoSyncConfigService implements AutoSyncConfigService {
  private final IndexInfoRepository indexInfoRepository;
  private final IndexDataRepository indexDataRepository;
  private final AutoSyncRepository autoSyncRepository;
  private final ExternalApiService externalApiService;
  private final AutoSyncMapper autoSyncMapper;
  private final IndexInfoMapper indexInfoMapper;
  private final IndexDataMapper indexDataMapper;

  /** 외부 API를 호출하여 지수 정보를 동기화하고 저장 */
  @Scheduled(fixedDelayString = "${batch.sync.info.fixed-delay}", zone = "Asia/Seoul")
  public void syncInfoAndSave() {
    try {
      // 1. 외부 API 호출하여 주식 시장 지수 데이터 가져오기
      OpenApiResponseDto apiResponse = externalApiService.fetchStockMarketIndex();

      // 유효성 검증
      if (apiResponse == null
          || apiResponse.response().body().items().item() == null
          || apiResponse.response().body().items().item().isEmpty()) {
        return;
      }

      int successCount = 0;
      int skipCount = 0;
      int errorCount = 0;

      // 2. API 응답의 각 아이템을 처리
      for (OpenApiResponseDto.IndexItemDto item : apiResponse.response().body().items().item()) {
        try {
          // 3. API 데이터를 IndexInfoCreateRequest로 변환
          IndexInfoCreateRequest request = convertApiItemToCreateRequest(item);

          // 4. createAutoSyncConfig 메소드 호출하여 DB에 저장
          createAutoSyncConfig(request);
          successCount++;

        } catch (IllegalStateException e) {
          // 중복 데이터인 경우 - 정상적인 상황으로 처리
          skipCount++;

        } catch (Exception e) {
          errorCount++;
        }
      }

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /** 활성화된 지수들에 대해 지수 데이터(시계열)를 주기적으로 가져와 동기화 */
  @Scheduled(fixedDelayString = "${batch.sync.enabled-index-data.fixed-delay}")
  public void syncEnabledIndexData() {

    List<IndexInfo> enabledIndices = indexInfoRepository.findAllByEnabledTrue();
    int totalIndexes  = enabledIndices.size();
    int successCount  = 0;
    int skipCount     = 0;
    int errorCount    = 0;

    LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

    for (IndexInfo indexInfo : enabledIndices) {
      LocalDate fromDate;
      try {
        // 1. 마지막으로 저장된 지수 데이터의 날짜 조회
        Optional<IndexData> latestDataOpt =
                indexDataRepository.findTopByIndexInfoOrderByBaseDateDesc(indexInfo);

        fromDate = latestDataOpt
                .map(d -> d.getBaseDate().plusDays(1))
                .orElse(indexInfo.getBasePointInTime());

        if (fromDate.isAfter(today)) {
          skipCount++;
          continue;
        }

        // 2. 외부 API 호출 후 DTO 리스트로 변환
        OpenApiResponseDto apiResp = externalApiService.fetchStockMarketIndex();
        IndexDataSyncRequest req  = new IndexDataSyncRequest(List.of(indexInfo.getId()), fromDate, today);
        List<IndexDataDto> dataDtos = toIndexDataDtoList(apiResp, indexInfo, req);

        if (dataDtos.isEmpty()) {
          skipCount++;
          continue;
        }

        // 3. DTO → Entity 변환
        List<IndexData> entities = dataDtos.stream()
                .map(dto -> {
                  try {
                    IndexData entity = indexDataMapper.toEntity(dto);
                    entity.setIndexInfo(indexInfo);
                    return entity;
                  } catch (Exception mapEx) {
                    return null;
                  }
                })
                .filter(Objects::nonNull)
                .toList();

        if (entities.isEmpty()) {
          skipCount++;
          continue;
        }

        // 4. 배치 저장
        try {
          indexDataRepository.saveAll(entities);
          successCount++;
        } catch (Exception saveEx) {
          errorCount++;
        }

      } catch (Exception e) {
        errorCount++;
      }
    }
  }

  @Override
  @Transactional
  public IndexInfoDto createAutoSyncConfig(IndexInfoCreateRequest request) {

    LocalDate basepointInTime = request.getBasePointInTime();

    // 중복 검사: classification + name + basepointInTime 기준
    Optional<IndexInfo> existing =
        indexInfoRepository.findFirstByIndexClassificationAndIndexNameOrderByCreatedAtDesc(
            request.getIndexClassification(), request.getIndexName());

    if (existing.isPresent()) {
      return indexInfoMapper.toIndexInfoDto(existing.get());
    }

    IndexInfo indexInfo = new IndexInfo();
    indexInfo.setIndexClassification(request.getIndexClassification());
    indexInfo.setIndexName(request.getIndexName());
    indexInfo.setEmployedItemsCount(request.getEmployedItemsCount());
    indexInfo.setBasePointInTime(basepointInTime);
    indexInfo.setBaseIndex(request.getBaseIndex());
    indexInfo.setSourceType(SourceType.OPEN_API);
    indexInfo.setFavorite(request.getFavorite());

    IndexInfo saved = indexInfoRepository.save(indexInfo);

    return indexInfoMapper.toIndexInfoDto(saved);
  }

  /* OpenApiItemDto를 IndexInfoCreateRequest로 변환 */
  private IndexInfoCreateRequest convertApiItemToCreateRequest(
      OpenApiResponseDto.IndexItemDto item) {
    IndexInfoCreateRequest request = new IndexInfoCreateRequest();

    request.setIndexClassification(item.idxCsf());
    request.setIndexName(item.idxNm());
    request.setEmployedItemsCount(item.epyItmsCnt());
    request.setBasePointInTime(parseStringToLocalDate(item.basPntm())); // Date 타입으로 가정
    request.setBaseIndex(item.basIdx());
    request.setFavorite(false); // 자동 동기화 데이터는 기본적으로 즐겨찾기 해제

    return request;
  }

  @Override
  public AutoSyncConfigDto updateEnabled(Long id, Boolean enabled) {
    IndexInfo entity =
        indexInfoRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("IndexInfo not found: " + id));

    entity.setEnabled(enabled);
    IndexInfo saved = indexInfoRepository.save(entity);
    return autoSyncMapper.toAutoSyncConfigDto(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public CursorPageResponseAutoSyncConfigDto listAutoSyncConfigs(
      Long indexId, Boolean enabled, Long lastId, int size, String sortBy, String sortDir) {
    // 1) Sort & Pageable 세팅
    Sort.Direction direction = Sort.Direction.fromString(sortDir);
    Pageable pageReq = PageRequest.of(0, size, Sort.by(direction, sortBy));

    // 2) 커서 페이징 조회
    List<IndexInfo> entities =
        autoSyncRepository.findByFilterAfterId(indexId, enabled, lastId, pageReq);

    // 3) 엔티티 → DTO 매핑
    List<AutoSyncConfigDto> content = autoSyncMapper.toAutoSyncConfigDtoList(entities);

    // 4) 다음 커서 계산
    long nextIdAfter = content.isEmpty() ? 0L : content.get(content.size() - 1).id();
    String nextCursor = nextIdAfter != 0L ? Long.toString(nextIdAfter) : null;

    boolean hasNext = content.size() == size;

    // 5) 응답 생성
    return new CursorPageResponseAutoSyncConfigDto(
        content, nextCursor, nextIdAfter, size, content.size(), hasNext);
  }

  /** 문자열 날짜를 LocalDate로 변환 (yyyyMMdd → LocalDate) */
  private LocalDate parseStringToLocalDate(String dateStr) {
    if (dateStr == null || dateStr.isBlank()) {
      return null;
    }

    String trimmed = dateStr.trim();
    DateTimeParseException lastException = null;

    List<DateTimeFormatter> formatters =
        List.of(
            DateTimeFormatter.BASIC_ISO_DATE, // "yyyyMMdd", ex: 20100401
            DateTimeFormatter.ISO_LOCAL_DATE // "yyyy-MM-dd", ex: 2010-04-01
            );

    for (DateTimeFormatter formatter : formatters) {
      try {
        return LocalDate.parse(trimmed, formatter);
      } catch (DateTimeParseException e) {
        // 실패하면 다음 포맷으로 넘어간다.
        lastException = e;
      }
    }
    // 어떤 것도 맞지 않으면 설명 있는 예외를 던진다
    throw new IllegalArgumentException(
        "지원하지 않는 날짜 형식입니다. 허용되는 형식: yyyyMMdd 또는 yyyy-MM-dd. 입력: " + dateStr);
  }

  private List<IndexDataDto> toIndexDataDtoList(
          OpenApiResponseDto responseDto,
          IndexInfo indexInfo,
          IndexDataSyncRequest request) {

    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMdd");
    String idxName = indexInfo.getIndexName();
    Long   idxId   = indexInfo.getId();  // indexInfoId 필드

    return responseDto
            .response()
            .body()
            .items()
            .item()
            .stream()
            // 1) 지수 이름 필터
            .filter(item -> idxName.equals(item.idxNm()))
            // 2) 날짜 범위 필터
            .filter(item -> {
              LocalDate d = LocalDate.parse(item.basDt(), fmt);
              return !d.isBefore(request.baseDateFrom()) &&
                      !d.isAfter(request.baseDateTo());
            })
            // 3) DTO 변환
            .map(item -> {
              LocalDate baseDate = LocalDate.parse(item.basDt(), fmt);
              return new IndexDataDto(
                      /* id */               null,
                      /* indexInfoId */      idxId,
                      /* baseDate */         baseDate,
                      /* sourceType */       SourceType.OPEN_API,
                      /* marketPrice */      item.mkp(),
                      /* closingPrice */     item.clpr(),
                      /* highPrice */        item.hipr(),
                      /* lowPrice */         item.lopr(),
                      /* versus */           item.vs(),
                      /* fluctuationRate */  item.fltRt(),
                      /* tradingQuantity */  item.trqu(),
                      /* tradingPrice */     item.trPrc(),
                      /* marketTotalAmount*/ item.lstgMrktTotAmt()
              );
            })
            .toList();
  }
}