package com.codeit.findex.mapper.file;

import com.codeit.findex.dto.IndexDataDto;
import com.codeit.findex.entity.IndexData;
import com.codeit.findex.mapper.GenericMapper;
import com.codeit.findex.request.IndexDataSaveRequest;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

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