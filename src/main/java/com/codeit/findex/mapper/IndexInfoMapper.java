package com.codeit.findex.mapper;

import com.codeit.findex.dto.indexInfo.request.IndexInfoCreateRequest;
import com.codeit.findex.dto.indexInfo.response.IndexInfoDto;
import com.codeit.findex.dto.indexInfo.response.IndexInfoSummaryDto;
import com.codeit.findex.entity.IndexInfo;
import com.codeit.findex.entityEnum.SourceType;
import java.util.List;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface IndexInfoMapper {

  IndexInfo toIndexInfo(IndexInfoDto dto);

  IndexInfoDto toIndexInfoDto(IndexInfo entity);

  List<IndexInfoDto> toIndexInfoDtoList(List<IndexInfo> indexInfos);

  IndexInfoSummaryDto toIndexInfoSummaryDto(IndexInfo indexInfo);

  @Mapping(target = "sourceType", expression = "java(com.codeit.findex.entityEnum.SourceType.USER)")
  IndexInfo IndexInfoCreateDtoToIndexInfo(IndexInfoCreateRequest dto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateInfoFromDto(IndexInfoDto dto, @MappingTarget IndexInfo entity);
}
