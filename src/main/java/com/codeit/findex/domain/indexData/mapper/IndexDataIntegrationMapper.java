package com.codeit.findex.domain.indexData.mapper;

import com.codeit.findex.domain.indexData.dto.response.IndexDataDto;
import com.codeit.findex.domain.base.entity.IndexData;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface IndexDataIntegrationMapper {

  IndexData toIndexData(IndexDataDto dto);

  IndexDataDto toIndexDataDto(IndexData dto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateDataFromDto(IndexDataDto dto, @MappingTarget IndexData entity);
}
