package com.codeit.findex.mapper;

import com.codeit.findex.dto.indexData.response.IndexDataDto;
import com.codeit.findex.entity.IndexData;
import com.codeit.findex.dto.indexData.request.IndexDataSaveRequest;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IndexDataMapper extends GenericMapper<IndexDataDto, IndexData>{

  IndexData toIndexData(IndexDataDto dto);

  IndexDataDto toIndexDataDto(IndexData dto);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateDataFromDto(IndexDataDto dto, @MappingTarget IndexData entity);


  // @Mapping에서 target은 바꿔야하는 변수
  IndexDataDto toDto(IndexData entity);
  IndexData toEntity(IndexDataDto dto);
  List<IndexDataDto> toDtos(List<IndexData> entities);
  List<IndexData> toEntities(List<IndexDataDto> dtos);

  IndexDataDto toDto(IndexDataSaveRequest request);
}