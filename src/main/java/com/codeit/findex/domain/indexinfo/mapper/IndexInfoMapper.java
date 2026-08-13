package com.codeit.findex.domain.indexinfo.mapper;

import com.codeit.findex.domain.indexinfo.dto.request.IndexInfoCreateRequest;
import com.codeit.findex.domain.indexinfo.dto.response.IndexInfoDto;
import com.codeit.findex.domain.indexinfo.dto.response.IndexInfoSummaryDto;
import com.codeit.findex.domain.base.entity.IndexInfo;
import java.util.List;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface IndexInfoMapper {

  IndexInfo toIndexInfo(IndexInfoDto dto);

  IndexInfoDto toIndexInfoDto(IndexInfo entity);

  List<IndexInfoDto> toIndexInfoDtoList(List<IndexInfo> indexInfos);

  IndexInfoSummaryDto toIndexInfoSummaryDto(IndexInfo indexInfo);

  @Mapping(target = "sourceType", expression = "java(com.codeit.findex.domain.base.entityEnum.SourceType.USER)")
  IndexInfo IndexInfoCreateDtoToIndexInfo(IndexInfoCreateRequest dto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateInfoFromDto(IndexInfoDto dto, @MappingTarget IndexInfo entity);
}
