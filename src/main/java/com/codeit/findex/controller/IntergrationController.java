package com.codeit.findex.controller;

import com.codeit.findex.dto.integration.CursorPageResponseSyncJobDto;
import com.codeit.findex.dto.integration.IndexDataSyncRequest;
import com.codeit.findex.dto.integration.SyncJobDto;
import com.codeit.findex.entityEnum.JobType;
import com.codeit.findex.entityEnum.Result;
import com.codeit.findex.service.Integration.IntegrationService;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sync-jobs")
public class IntergrationController {

  private final IntegrationService integrationService;

  @PostMapping("/index-infos")
  public ResponseEntity<List<SyncJobDto>> integrateIndexInfo(HttpServletRequest request) {
    List<SyncJobDto> syncJobDtos = integrationService.integrateIndexInfo(request);
    return ResponseEntity.ok(syncJobDtos);
  }

  @PostMapping("/index-data")
  public ResponseEntity<List<SyncJobDto>> integrateIndexData(
      @RequestBody IndexDataSyncRequest indexDataSyncRequest, HttpServletRequest request) {
    List<SyncJobDto> syncJobDtos =
        integrationService.integrateIndexData(indexDataSyncRequest, request);
    return ResponseEntity.ok(syncJobDtos);
  }

  @GetMapping
  public ResponseEntity<CursorPageResponseSyncJobDto> getCursorPageResponseSyncJobDto(
      @ModelAttribute JobType jobType,
      IndexDataSyncRequest indexDataSyncRequest,
      String worker,
      LocalDateTime jobTimeFrom,
      LocalDateTime jobTimeTo,
      Result status,
      long idAfter,
      String cursor,
      String sortField,
      String sortDirection,
      int size) {
    CursorPageResponseSyncJobDto cursorPageResponseSyncJobDto =
        integrationService.integrateCursorPage(
            jobType,
            indexDataSyncRequest,
            worker,
            jobTimeFrom,
            jobTimeTo,
            status,
            idAfter,
            cursor,
            sortField,
            sortDirection,
            size);
    return ResponseEntity.ok(cursorPageResponseSyncJobDto);
  }
}
