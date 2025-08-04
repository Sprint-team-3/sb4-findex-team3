package com.codeit.findex.mapper;

import com.codeit.findex.dto.indexData.request.IndexDataSaveRequest;
import com.codeit.findex.dto.indexData.response.IndexDataDto;
import com.codeit.findex.entity.IndexData;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;

import java.util.List;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface IndexDataMapper {

    // @Mapping에서 target은 바꿔야하는 변수
    IndexDataDto toDto(IndexData entity);
    IndexData toEntity(IndexDataDto dto);

    IndexData toIndexData(IndexDataDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateDataFromDto(IndexDataDto dto, @MappingTarget IndexData entity);
}
