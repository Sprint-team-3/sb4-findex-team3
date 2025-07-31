package com.codeit.findex.controller;

import com.codeit.findex.dto.autosync.response.AutoSyncConfigDto;
import com.codeit.findex.service.autosync.AutoSyncConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auto-sync-configs")
@RequiredArgsConstructor
public class AutoSyncConfigController {
    private final AutoSyncConfigService autoSyncConfigService;

    @PatchMapping("/{id}")
    public ResponseEntity<AutoSyncConfigDto> updateEnabled(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> request
    ) {
        Boolean enabled = request.get("enabled");
        AutoSyncConfigDto dto = autoSyncConfigService.updateEnabled(id, enabled);
        return ResponseEntity.ok(dto);
    }
}
