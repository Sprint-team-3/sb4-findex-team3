package com.codeit.findex.mapper;

import com.codeit.findex.dto.autosync.response.AutoSyncConfigDto;
import com.codeit.findex.dto.autosync.response.CursorPageResponseAutoSyncConfigDto;
import com.codeit.findex.entity.IndexInfo;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface AutoSyncMapper {

  /** 단일 IndexInfo → AutoSyncConfigDto 매핑 */
  @Mapping(source = "indexInfo.id", target = "indexInfoId")
  @Mapping(source = "indexInfo.indexClassification", target = "indexClassification")
  @Mapping(source = "indexInfo.indexName", target = "indexName")
  AutoSyncConfigDto toAutoSyncConfigDto(IndexInfo indexInfo);

  /** 엔티티 리스트 → DTO 리스트 매핑 */
  List<AutoSyncConfigDto> toAutoSyncConfigDtoList(List<IndexInfo> indexInfoList);

  /**
   * Page<IndexInfo> → CursorPageResponseAutoSyncConfigDto 변환 - content: 매핑된 DTO 리스트 - nextCursor:
   * 마지막 DTO의 id를 문자열로 - nextIdAfter: 마지막 DTO의 id - size, totalElements, hasNext: Page 객체에서 그대로
   */
  default CursorPageResponseAutoSyncConfigDto toCursorPageResponseAutoSyncConfigDto(
      Page<IndexInfo> indexInfoPage) {
    List<AutoSyncConfigDto> content = toAutoSyncConfigDtoList(indexInfoPage.getContent());
    long nextIdAfter = !content.isEmpty() ? content.get(content.size() - 1).id() : 0L;
    String nextCursor = nextIdAfter != 0L ? Long.toString(nextIdAfter) : null;

    return new CursorPageResponseAutoSyncConfigDto(
        content,
        nextCursor,
        nextIdAfter,
        indexInfoPage.getSize(),
        indexInfoPage.getTotalElements(),
        indexInfoPage.hasNext());
  }
}
