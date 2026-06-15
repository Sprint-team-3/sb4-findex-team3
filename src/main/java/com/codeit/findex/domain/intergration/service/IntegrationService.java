 package com.codeit.findex.domain.intergration.service;

 import com.codeit.findex.domain.intergration.dto.CursorPageResponseSyncJobDto;
 import com.codeit.findex.domain.intergration.dto.IndexDataSyncRequest;
 import com.codeit.findex.domain.intergration.dto.SyncJobDto;
 import com.codeit.findex.domain.base.entityEnum.JobType;
 import com.codeit.findex.domain.base.entityEnum.Result;
 import jakarta.servlet.http.HttpServletRequest;
 import java.time.LocalDate;
 import java.time.LocalDateTime;
 import java.util.List;

 public interface IntegrationService {
  List<SyncJobDto> integrateIndexInfo(HttpServletRequest request);

  List<SyncJobDto> integrateIndexData(
      IndexDataSyncRequest indexDataSyncRequest, HttpServletRequest request);

  CursorPageResponseSyncJobDto integrateCursorPage(
      JobType jobType,
      Long indexInfoId,
      LocalDate baseDateFrom,
      LocalDate baseDateTo,
      String worker,
      LocalDateTime jobTimeFrom,
      LocalDateTime jobTimeTo,
      Result status,
      Long idAfter,
      String cursor,
      String sortField,
      String sortDirection,
      int size);
 }
