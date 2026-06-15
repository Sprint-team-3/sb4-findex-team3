package com.codeit.findex.domain.autosync.mapper;

import com.codeit.findex.domain.autosync.dto.response.AutoSyncConfigDto;
import com.codeit.findex.domain.base.entity.IndexInfo;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AutoSyncMapper {

  @Mapping(source = "indexInfo.id", target = "indexInfoId")
  @Mapping(source = "indexInfo.indexClassification", target = "indexClassification")
  @Mapping(source = "indexInfo.indexName", target = "indexName")
  AutoSyncConfigDto toAutoSyncConfigDto(IndexInfo indexInfo);

  List<AutoSyncConfigDto> toAutoSyncConfigDtoList(List<IndexInfo> indexInfoList);
}
