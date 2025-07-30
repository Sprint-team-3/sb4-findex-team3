package com.codeit.findex.repository;

import com.codeit.findex.dto.indexInfo.request.IndexInfoSearchCond;
import com.codeit.findex.dto.indexInfo.response.IndexInfoDto;

import java.util.List;

public interface IndexInfoRepositoryCustom {
    List<IndexInfoDto> findBySearchCond(IndexInfoSearchCond cond);

}
