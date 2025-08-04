package com.codeit.findex.service.Integration;

import com.codeit.findex.dto.integration.CursorPageResponseSyncJobDto;
import com.codeit.findex.dto.integration.IndexDataSyncRequest;
import com.codeit.findex.dto.integration.SyncJobDto;
import com.codeit.findex.entityEnum.JobType;
import com.codeit.findex.entityEnum.Result;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface IntegrationService {
  List<SyncJobDto> integrateIndexInfo(HttpServletRequest request);

  List<SyncJobDto> integrateIndexData(
      IndexDataSyncRequest indexDataSyncRequest, HttpServletRequest request);

  CursorPageResponseSyncJobDto integrateCursorPage(
      JobType jobType,
      IndexDataSyncRequest indexDataSyncRequest,
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
