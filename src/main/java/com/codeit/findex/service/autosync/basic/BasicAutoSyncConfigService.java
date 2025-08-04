package com.codeit.findex.service.autosync.basic;

import com.codeit.findex.dto.autosync.response.AutoSyncConfigDto;
import com.codeit.findex.dto.autosync.response.CursorPageResponseAutoSyncConfigDto;
import com.codeit.findex.dto.dashboard.OpenApiResponseDto;
import com.codeit.findex.dto.indexInfo.request.IndexInfoCreateRequest;
import com.codeit.findex.dto.indexInfo.response.IndexInfoDto;
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
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private static final Logger log = LoggerFactory.getLogger(BasicAutoSyncConfigService.class);

  /** 매일 새벽 1시에 외부 API를 호출하여 지수 정보를 동기화하고 저장 */
  @Scheduled(fixedDelayString = "${batch.sync.info.fixed-delay}", zone = "Asia/Seoul")
  public void syncInfoAndSave() {

    log.info("=== 지수 데이터 자동 동기화 시작 ===");

    try {
      // 1. 외부 API 호출하여 주식 시장 지수 데이터 가져오기
      OpenApiResponseDto apiResponse = externalApiService.fetchStockMarketIndex();

      // 유효성 검증
      if (apiResponse == null
          || apiResponse.response().body().items().item() == null
          || apiResponse.response().body().items().item().isEmpty()) {
        log.warn("외부 API에서 받아온 데이터가 없습니다.");
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

          log.debug(
              "지수 정보 저장 성공: {} - {}", request.getIndexClassification(), request.getIndexName());

        } catch (IllegalStateException e) {
          // 중복 데이터인 경우 - 정상적인 상황으로 처리
          skipCount++;
          log.debug("중복 데이터로 인한 스킵: {} - {}", item.idxCsf(), item.idxNm());

        } catch (Exception e) {
          errorCount++;
          log.error(
              "지수 정보 저장 실패 - 분류: {}, 지수명: {}, 오류: {}", item.idxCsf(), item.idxNm(), e.getMessage());
        }
      }

      log.info(
          "=== 지수 데이터 동기화 완료 - 성공: {}건, 스킵: {}건, 실패: {}건 ===", successCount, skipCount, errorCount);

    } catch (Exception e) {
      log.error("지수 데이터 자동 동기화 중 전체 오류 발생", e);
    }
  }

  /** 활성화된 지수들에 대해 지수 데이터(시계열)를 주기적으로 가져와 동기화 */
  @Scheduled(fixedDelayString = "${batch.sync.enabled-index-data.fixed-delay}", zone = "Asia/Seoul")
  public void syncEnabledIndexData() {
    log.info("=== 활성화된 지수 데이터 자동 연동 시작 ===");

    List<IndexInfo> enabledIndices = indexInfoRepository.findAllByEnabledTrue();
    int totalIndexes = enabledIndices.size();
    int successCount = 0;
    int skipCount = 0;
    int errorCount = 0;

    LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

    for (IndexInfo indexInfo : enabledIndices) {
      try {
        // 1. 마지막으로 저장된 지수 데이터의 날짜 조회
        Optional<IndexData> latestDataOpt =
            indexDataRepository.findTopByIndexInfoOrderByBaseDateDesc(indexInfo);

        LocalDate fromDate;
        if (latestDataOpt.isPresent()) {
          LocalDate lastSyncDate = latestDataOpt.get().getBaseDate();
          fromDate = lastSyncDate.plusDays(1); // 이미 있는 날짜 다음부터
        } else {
          // 이전 데이터가 없으면, 기준 시점(basepointInTime)부터 시작
          fromDate = indexInfo.getBasepointInTime();
        }

        if (fromDate.isAfter(today)) {
          // 이미 최신 상태
          skipCount++;
          log.debug(
              "동기화 대상 없음 (이미 최신): {} - {} (fromDate={}, today={})",
              indexInfo.getIndexClassification(),
              indexInfo.getIndexName(),
              fromDate,
              today);
          continue;
        }

        // 2. 외부 API 호출: fromDate ~ today 사이 데이터 가져오기
        List<OpenApiResponseDto.IndexItemDto> dataItems =
            externalApiService.fetchIndexData(indexInfo, fromDate, today);

        if (dataItems == null || dataItems.isEmpty()) {
          skipCount++;
          log.debug(
              "외부 API에 해당 기간 데이터 없음: {} - {} (from={}, to={})",
              indexInfo.getIndexClassification(),
              indexInfo.getIndexName(),
              fromDate,
              today);
          continue;
        }

        // 3. 가져온 데이터를 엔티티로 변환 후 저장
        for (OpenApiResponseDto.IndexItemDto item : dataItems) {
          try {
            IndexData indexData = indexDataMapper.toIndexData(indexInfo, item);
            indexDataRepository.save(indexData);
          } catch (Exception e) {
            log.warn(
                "지수 데이터 저장 실패 (개별 항목) - {} - {}: {}",
                indexInfo.getIndexClassification(),
                indexInfo.getIndexName(),
                e.getMessage(),
                e);
          }
        }

        successCount++;
        log.info(
            "지수 데이터 동기화 완료: {} - {} (from={}, to={}, items={})",
            indexInfo.getIndexClassification(),
            indexInfo.getIndexName(),
            fromDate,
            today,
            dataItems.size());

      } catch (Exception e) {
        errorCount++;
        log.error(
            "지수 데이터 동기화 중 오류 발생 - {} - {}: {}",
            indexInfo.getIndexClassification(),
            indexInfo.getIndexName(),
            e.getMessage(),
            e);
      }
    }

    log.info(
        "=== 활성화된 지수 데이터 자동 연동 종료 (대상 {}개) - 성공: {}건, 스킵: {}건, 실패: {}건 ===",
        totalIndexes,
        successCount,
        skipCount,
        errorCount);
  }

  @Override
  public IndexInfoDto createAutoSyncConfig(IndexInfoCreateRequest request) {

    LocalDate basepointInTime = request.getBasepointInTime();

    // 중복 검사: classification + name + basepointInTime 기준
    Optional<IndexInfo> existing =
            indexInfoRepository.findFirstByIndexClassificationAndIndexNameAndBasepointInTimeOrderByCreatedAtDesc(
                    request.getIndexClassification(), request.getIndexName(), basepointInTime);

    if (existing.isPresent()) {
      log.debug(
              "이미 존재하는 지수 설정 - 등록 스킵: {} - {}",
              request.getIndexClassification(),
              request.getIndexName());
      return indexInfoMapper.toIndexInfoDto(existing.get());
    }

    IndexInfo indexInfo = new IndexInfo();
    indexInfo.setIndexClassification(request.getIndexClassification());
    indexInfo.setIndexName(request.getIndexName());
    indexInfo.setEmployedItemsCount(request.getEmployedItemsCount());
    indexInfo.setBasepointInTime(basepointInTime);
    indexInfo.setBaseIndex(request.getBaseIndex());
    indexInfo.setSourceType(SourceType.OPEN_API);
    indexInfo.setFavorite(request.getFavorite());
    indexInfo.setEnabled(false); // 등록 시 비활성화

    System.out.println("indexInfo = " + indexInfo);

    IndexInfo saved = indexInfoRepository.save(indexInfo);
    log.info("새로운 지수 설정 등록: {} - {}", request.getIndexClassification(), request.getIndexName());

    System.out.println("saved = " + saved);
    return indexInfoMapper.toIndexInfoDto(saved);
  }

  /** OpenApiItemDto를 IndexInfoCreateRequest로 변환 */
  private IndexInfoCreateRequest convertApiItemToCreateRequest(
      OpenApiResponseDto.IndexItemDto item) {
    IndexInfoCreateRequest request = new IndexInfoCreateRequest();

    request.setIndexClassification(item.idxCsf());
    request.setIndexName(item.idxNm());
    request.setEmployedItemsCount(item.epyItmsCnt());
    request.setBasepointInTime(parseStringToLocalDate(item.basPntm())); // Date 타입으로 가정
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
        log.debug("날짜 '{}' 를 포맷 '{}' 으로 파싱하지 못함, 다음 포맷 시도...", trimmed, formatter);
      }
    }
    // 어떤 것도 맞지 않으면 설명 있는 예외를 던진다
    throw new IllegalArgumentException(
            "지원하지 않는 날짜 형식입니다. 허용되는 형식: yyyyMMdd 또는 yyyy-MM-dd. 입력: " + dateStr);
  }
}
