/*
 package com.codeit.findex.controller;

 import com.codeit.findex.dto.integration.IndexDataSyncRequest;
 import com.codeit.findex.dto.integration.SyncJobDto;
 import com.codeit.findex.service.IntegrationService;
 import jakarta.servlet.http.HttpServletRequest;
 import java.util.List;
 import lombok.RequiredArgsConstructor;
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
  public List<SyncJobDto> integrateIndexInfo(HttpServletRequest request) {
    return integrationService.integrateIndexInfo(request);
  }

  @PostMapping("/index-data")
  public List<SyncJobDto> integrateIndexData(
      @RequestBody IndexDataSyncRequest indexDataSyncRequest, HttpServletRequest request) {
    return integrationService.integrateIndexData(indexDataSyncRequest, request);
  }
  //
  //  @GetMapping
  //  public CursorPageResponseSyncJobDto getCursorPageResponseSyncJobDto(@ModelAttribute JobType
  // jobType,
  //      IndexDataSyncRequest indexDataSyncRequest,
  //      String worker,
  //      Date jobTimeFrom,
  //      Date jobTimeTo,
  //      boolean status,
  //      long idAfter,
  //      String cursor,
  //      String sortField,
  //      String sortDirection,
  //      int size){
  //
  //  }

 }
*/
