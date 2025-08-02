package com.codeit.findex.service.basic;

import com.codeit.findex.dto.indexInfo.request.IndexInfoCreateRequest;
import com.codeit.findex.dto.indexInfo.request.IndexInfoSearchCond;
import com.codeit.findex.dto.indexInfo.request.IndexInfoUpdateRequest;
import com.codeit.findex.dto.indexInfo.response.CursorPageResponseIndexInfoDto;
import com.codeit.findex.dto.indexInfo.response.IndexInfoDto;
import com.codeit.findex.dto.indexInfo.response.IndexInfoSummaryDto;
import com.codeit.findex.entity.IndexData;
import com.codeit.findex.entity.IndexInfo;
import com.codeit.findex.mapper.IndexInfoMapper;
import com.codeit.findex.repository.IndexDataRepository;
import com.codeit.findex.repository.IndexInfoRepository;
import com.codeit.findex.service.IndexInfoService;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicIndexInfoService implements IndexInfoService {
  private final IndexInfoRepository indexInfoRepository;
  private final IndexInfoMapper indexInfoMapper;
  private final IndexDataRepository indexDataRepository;

  // 컨트롤러가 호출하는 메서드
  @Transactional
  @Override
  public CursorPageResponseIndexInfoDto findBySearchCondWithPaging(IndexInfoSearchCond cond) {

    // 1. cond 객체로부터 정렬(Sort) 객체 생성
    Sort sort = createSort(cond.getSortField(), cond.getSortDirection());

    // 2. cond 객체로부터 Pageable 생성
    Pageable pageable = PageRequest.of(0, cond.getSize(), sort);

    String indexName = (cond.getIndexName() != null && cond.getIndexName().trim().isEmpty()) ? null : cond.getIndexName();
    String classification = (cond.getIndexClassification() != null && cond.getIndexClassification().trim().isEmpty()) ? null : cond.getIndexClassification();

    // 3. Repository 호출
    Slice<IndexInfo> resultSlice = indexInfoRepository.findBySearchCondWithPaging(
            indexName,
            classification,
            cond.getFavorite(),
            cond.getIdAfter(),
            pageable
    );

    // 4. Slice<IndexInfo>를 최종 응답 DTO인 CursorPageResponseIndexInfoDto로 변환
    List<IndexInfo> content = resultSlice.getContent();
    List<IndexInfoDto> dtoContent = content.stream()
            .map(indexInfoMapper::toIndexInfoDto)
            .toList();

    boolean hasNext = resultSlice.hasNext();
    long nextIdAfter = !content.isEmpty() ? content.get(content.size() - 1).getId() : 0L;

    return new CursorPageResponseIndexInfoDto(
            dtoContent,  // 변환된 DTO 리스트
            null,        // nextCursor(필요 시 구현)
            nextIdAfter,
            cond.getSize(),  // 한 페이지 크기
            -1,              // totalElements (필요 시 구현)
            hasNext
    );
  }

  private Sort createSort(String sortField, String sortDirection) {
    // 정렬 방향 결정
    Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;

    String sortProperty;
    // API 요청 파라미터(sortField)와 엔티티 필드명을 매핑
    switch (sortField) {
      case "indexClassification":
        sortProperty = "indexClassification";
        break;
      case "employedItemsCount":
        sortProperty = "employedItemsCount";
        break;
      case "indexName":
      default: // 기본 정렬 필드
        sortProperty = "indexName";
        break;
    }

    // 안정적인 페이징을 위해 항상 id 오름차순을 두 번째 정렬 조건으로 추가
    return Sort.by(direction, sortProperty)
            .and(Sort.by(Sort.Direction.ASC, "id"));
  }

  // 즐겨찾기만으로 조회하는 메서드
  @Transactional
  @Override
  public CursorPageResponseIndexInfoDto findByFavoriteWithPaging(Boolean favorite, Long idAfter, Integer size) {
    int pageSize = size != null ? size : 10;
    
    // Pageable 생성 (limit + 1로 조회하여 hasNext 확인)
    Pageable pageable = PageRequest.of(0, pageSize + 1);
    
    // Slice로 조회
    Slice<IndexInfo> slice = indexInfoRepository.findByFavoriteWithPaging(
            favorite, idAfter, pageable);

    // 페이징 정보 계산
    boolean hasNext = slice.hasNext();
    List<IndexInfo> content = slice.getContent();
    
    // 실제 페이지 크기만큼만 반환
    List<IndexInfo> actualContent = hasNext ? content.subList(0, pageSize) : content;
    
    // 엔티티 → DTO로 변환
    List<IndexInfoDto> dtoContent = actualContent.stream()
            .map(indexInfoMapper::toIndexInfoDto)
            .toList();

    // 다음 페이지 커서 계산
    long nextIdAfter = !actualContent.isEmpty() ? actualContent.get(actualContent.size() - 1).getId() : 0L;

    return new CursorPageResponseIndexInfoDto(
            dtoContent, null, nextIdAfter, pageSize, -1, hasNext
    );
  }

  @Transactional
  @Override
  public IndexInfoDto findIndexInfoById(long id) {
    IndexInfo indexInfo =
        indexInfoRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Index info with" + id + "not found"));
    return indexInfoMapper.toIndexInfoDto(indexInfo);
  }

  @Transactional
  @Override
  public List<IndexInfoSummaryDto> findIndexInfoSummaries() {
    return indexInfoRepository.findAll().stream()
        .map(indexInfoMapper::toIndexInfoSummaryDto)
        .toList();
  }

  @Transactional
  @Override
  public IndexInfoDto registerIndexInfo(IndexInfoCreateRequest dto) {
    if (dto == null) {
      throw new IllegalArgumentException("Request cannot be null");
    }
    if (dto.getIndexName() == null || dto.getIndexName().trim().isEmpty()) {
      throw new IllegalArgumentException("Index name cannot be empty");
    }
    if (dto.getIndexClassification() == null || dto.getIndexClassification().trim().isEmpty()) {
      throw new IllegalArgumentException("IndexClassification cannot be empty");
    }
    if (indexInfoRepository.existsByIndexName(dto.getIndexName())) {
      throw new IllegalArgumentException("Index name" + dto.getIndexName() + " already exists");
    }
    IndexInfo indexInfo = indexInfoMapper.IndexInfoCreateDtoToIndexInfo(dto);
    indexInfoRepository.save(indexInfo);
    return indexInfoMapper.toIndexInfoDto(indexInfo);
  }

  @Transactional
  @Override
  public IndexInfoDto updateIndexInfo(long id, IndexInfoUpdateRequest dto) {
    IndexInfo indexInfo =
        indexInfoRepository
            .findById(id)
            .orElseThrow(
                () -> new IllegalArgumentException("Index info with ID" + id + "not found"));
    if (dto.getEmployedItemsCount() != null) {
      indexInfo.setEmployedItemsCount(dto.getEmployedItemsCount());
    }
    if (dto.getBasePointInTime() != null) {
      indexInfo.setBasepointInTime(dto.getBasePointInTime());
    }
    if (dto.getBaseIndex() != null) {
      indexInfo.setBaseIndex(dto.getBaseIndex());
    }
    if (dto.getFavorite() != null) {
      indexInfo.setFavorite(dto.getFavorite());
    }
    return indexInfoMapper.toIndexInfoDto(indexInfo);
  }

  @Transactional
  @Override
  public void deleteIndexInfo(long id) {
    IndexInfo indexInfo =
        indexInfoRepository
            .findById(id)
            .orElseThrow(
                () -> new IllegalArgumentException("Index info with ID" + id + "not found"));
    List<IndexData> indexDataList = indexDataRepository.findAllByIndexInfoId(id);
    if (!indexDataList.isEmpty()) {
      indexDataRepository.deleteAll(indexDataList);
    }
    indexInfoRepository.delete(indexInfo);
  }
}
