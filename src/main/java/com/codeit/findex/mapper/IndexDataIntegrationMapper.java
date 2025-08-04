package com.codeit.findex.mapper;

import com.codeit.findex.dto.indexData.response.IndexDataDto;
import com.codeit.findex.entity.IndexData;
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