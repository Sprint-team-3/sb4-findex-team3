package com.codeit.findex.mapper;

import com.codeit.findex.dto.indexData.response.IndexDataDto;
import com.codeit.findex.entity.IndexData;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface IndexDataMapper {

    // @Mapping에서 source가 변수 파라미터(매개변수), target이 리턴타입
    @Mapping(source = "indexInfo.id", target = "indexInfoId")
    IndexDataDto toDto(IndexData entity);

    IndexData toIndexData(IndexDataDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateDataFromDto(IndexDataDto dto, @MappingTarget IndexData entity);
}