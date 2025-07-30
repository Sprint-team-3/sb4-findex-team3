package com.codeit.findex.service;

import com.codeit.findex.dto.integration.IndexDataSyncRequest;
import com.codeit.findex.dto.integration.SyncJobDto;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

public interface IntegrationService {
  List<SyncJobDto> integrateIndexInfo(HttpServletRequest request);

  List<SyncJobDto> integrateIndexData(
      IndexDataSyncRequest indexDataSyncRequest, HttpServletRequest request);
}
