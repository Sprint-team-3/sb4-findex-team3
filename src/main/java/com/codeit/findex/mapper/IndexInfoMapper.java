package com.codeit.findex.mapper;

import com.codeit.findex.dto.IndexInfoDto;
import com.codeit.findex.entity.IndexInfo;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface IndexInfoMapper {
  IndexInfo toIndexInfo(IndexInfoDto dto);

  IndexInfoDto toIndexInfoDto(IndexInfo dto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateInfoFromDto(IndexInfoDto dto, @MappingTarget IndexInfo entity);
}
