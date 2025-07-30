package com.codeit.findex.mapper;

import com.codeit.findex.dto.indexInfo.response.IndexInfoDto;
import com.codeit.findex.entity.IndexInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IndexInfoMapper {
    IndexInfoDto toIndexInfoDto(IndexInfo indexInfo);
    IndexInfo toIndexInfo(IndexInfoDto indexInfoDto);

}
