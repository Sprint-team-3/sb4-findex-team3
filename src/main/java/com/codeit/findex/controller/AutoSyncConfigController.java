package com.codeit.findex.controller;

import com.codeit.findex.dto.autosync.response.AutoSyncConfigDto;
import com.codeit.findex.dto.autosync.response.CursorPageResponseAutoSyncConfigDto;
import com.codeit.findex.service.autosync.AutoSyncConfigService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auto-sync-configs")
@RequiredArgsConstructor
public class AutoSyncConfigController {
  private final AutoSyncConfigService autoSyncConfigService;

  @PatchMapping("/{id}")
  public ResponseEntity<AutoSyncConfigDto> updateEnabled(
      @PathVariable Long id, @RequestBody Map<String, Boolean> request) {
    Boolean enabled = request.get("enabled");
    AutoSyncConfigDto dto = autoSyncConfigService.updateEnabled(id, enabled);
    return ResponseEntity.ok(dto);
  }

  /**
   * GET /api/auto-sync-configs
   *
   * @param indexInfoId (optional) 지수 ID 필터
   * @param enabled (optional) 활성화 여부 필터
   * @param idAfter (optional) 이전 페이지 마지막 요소 ID (커서)
   * @param size (optional) 한 페이지 크기, 기본 20
   * @param sortBy (optional) 정렬 컬럼, 기본 "id"
   * @param sortDir (optional) 정렬 방향, 기본 "asc"
   */
  @GetMapping
  public ResponseEntity<CursorPageResponseAutoSyncConfigDto> list(
      @RequestParam(required = false) Long indexInfoId, // indexId
      @RequestParam(required = false) Boolean enabled,
      @RequestParam(required = false) Long idAfter,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "asc") String sortDir) {
    CursorPageResponseAutoSyncConfigDto dto =
        autoSyncConfigService.listAutoSyncConfigs(
            indexInfoId, enabled, idAfter, size, sortBy, sortDir);
    return ResponseEntity.ok(dto);
  }
}
