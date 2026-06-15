package com.codeit.findex.domain.indexinfo.service;

import com.codeit.findex.domain.indexinfo.dto.request.IndexInfoCreateRequest;
import com.codeit.findex.domain.indexinfo.dto.request.IndexInfoSearchCond;
import com.codeit.findex.domain.indexinfo.dto.request.IndexInfoUpdateRequest;
import com.codeit.findex.domain.indexinfo.dto.response.CursorPageResponseIndexInfoDto;
import com.codeit.findex.domain.indexinfo.dto.response.IndexInfoDto;
import com.codeit.findex.domain.indexinfo.dto.response.IndexInfoSummaryDto;
import java.util.List;

public interface IndexInfoService {
  CursorPageResponseIndexInfoDto findBySearchCondWithPaging(IndexInfoSearchCond cond);

  // 즐겨찾기만으로 조회하는 메서드
  List<IndexInfoDto> findAllByFavorite(Boolean favorite);

  IndexInfoDto findIndexInfoById(long id);

  List<IndexInfoSummaryDto> findIndexInfoSummaries();

  IndexInfoDto registerIndexInfo(IndexInfoCreateRequest dto);

  IndexInfoDto updateIndexInfo(long id, IndexInfoUpdateRequest dto);

  void deleteIndexInfo(long id);
}
