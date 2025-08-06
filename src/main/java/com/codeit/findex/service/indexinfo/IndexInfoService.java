package com.codeit.findex.service.indexinfo;

import com.codeit.findex.dto.indexInfo.request.IndexInfoCreateRequest;
import com.codeit.findex.dto.indexInfo.request.IndexInfoSearchCond;
import com.codeit.findex.dto.indexInfo.request.IndexInfoUpdateRequest;
import com.codeit.findex.dto.indexInfo.response.CursorPageResponseIndexInfoDto;
import com.codeit.findex.dto.indexInfo.response.IndexInfoDto;
import com.codeit.findex.dto.indexInfo.response.IndexInfoSummaryDto;
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
