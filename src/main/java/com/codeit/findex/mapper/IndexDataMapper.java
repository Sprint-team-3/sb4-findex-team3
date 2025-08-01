package com.codeit.findex.mapper;

import com.codeit.findex.dto.indexData.request.IndexDataSaveRequest;
import com.codeit.findex.dto.indexData.response.IndexDataDto;
import com.codeit.findex.entity.IndexData;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IndexDataMapper extends GenericMapper<IndexDataDto, IndexData> {

    // @Mapping에서 target은 바꿔야하는 변수
    IndexDataDto toDto(IndexData entity);
    IndexData toEntity(IndexDataDto dto);
    List<IndexDataDto> toDtos(List<IndexData> entities);
    List<IndexData> toEntities(List<IndexDataDto> dtos);

    IndexDataDto toDto(IndexDataSaveRequest request);

}
