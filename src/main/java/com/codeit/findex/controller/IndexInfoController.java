package com.codeit.findex.controller;

import com.codeit.findex.controller.api.IndexInfoApi;
import com.codeit.findex.dto.indexInfo.request.IndexInfoCreateRequest;
import com.codeit.findex.dto.indexInfo.request.IndexInfoSearchCond;
import com.codeit.findex.dto.indexInfo.request.IndexInfoUpdateRequest;
import com.codeit.findex.dto.indexInfo.response.CursorPageResponseIndexInfoDto;
import com.codeit.findex.dto.indexInfo.response.IndexInfoDto;
import com.codeit.findex.dto.indexInfo.response.IndexInfoSummaryDto;
import com.codeit.findex.service.basic.BasicIndexInfoService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/index-infos")
public class IndexInfoController implements IndexInfoApi {

  private final BasicIndexInfoService basicIndexInfoService;

  @GetMapping
  public ResponseEntity<CursorPageResponseIndexInfoDto> getIndexInfoList(
      @ModelAttribute IndexInfoSearchCond cond) {
    CursorPageResponseIndexInfoDto response =
        basicIndexInfoService.findBySearchCondWithPaging(cond);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<IndexInfoDto> getIndexInfo(@PathVariable long id) {
    return ResponseEntity.ok(basicIndexInfoService.findIndexInfoById(id));
  }

  @GetMapping("/summaries")
  public ResponseEntity<List<IndexInfoSummaryDto>> getIndexInfoSummaries() {
    return ResponseEntity.ok(basicIndexInfoService.findIndexInfoSummaries());
  }

  @PostMapping
  public ResponseEntity<IndexInfoDto> registerIndexInfo(
      @RequestBody IndexInfoCreateRequest request) {
    IndexInfoDto response = basicIndexInfoService.registerIndexInfo(request);
    return ResponseEntity.ok(response);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<IndexInfoDto> updateIndexInfo(
      @PathVariable long id, @RequestBody IndexInfoUpdateRequest request) {
    IndexInfoDto response = basicIndexInfoService.updateIndexInfo(id, request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity deleteIndexInfo(@PathVariable long id) {
    basicIndexInfoService.deleteIndexInfo(id);
    return ResponseEntity.ok().build();
  }
}
