package com.codeit.findex.mapper;

import com.codeit.findex.dto.autosync.response.AutoSyncConfigDto;
import com.codeit.findex.entity.IndexInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AutoSyncMapper {

    @Mapping(source = "indexInfo.id", target = "indexInfoId")
    @Mapping(source = "indexInfo.indexClassification", target = "indexClassification")
    @Mapping(source = "indexInfo.indexName", target = "indexName")
    @Mapping(source = "active", target = "enabled")
    AutoSyncConfigDto toAutoSyncConfigDto(IndexInfo indexInfo);

    List<AutoSyncConfigDto> toAutoSyncConfigDtoList(List<IndexInfo> indexInfoList);
}
