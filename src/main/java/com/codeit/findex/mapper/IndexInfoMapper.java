package com.codeit.findex.mapper;

import com.codeit.findex.dto.indexInfo.request.IndexInfoCreateRequest;
import com.codeit.findex.dto.indexInfo.response.IndexInfoDto;
import com.codeit.findex.dto.indexInfo.response.IndexInfoSummaryDto;
import com.codeit.findex.entity.IndexInfo;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IndexInfoMapper {
    IndexInfo toIndexInfo(IndexInfoDto dto );

    IndexInfoDto toIndexInfoDto(IndexInfo indexInfo);

    List<IndexInfoDto> toIndexInfoDtoList(List<IndexInfo> indexInfos);

    IndexInfoSummaryDto toIndexInfoSummaryDto(IndexInfo indexInfo);

    IndexInfo IndexInfoCreateDtoToIndexInfo(IndexInfoCreateRequest dto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateInfoFromDto(IndexInfoDto dto, @MappingTarget IndexInfo entity);
}
