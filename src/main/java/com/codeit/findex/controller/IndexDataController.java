package com.codeit.findex.controller;

import com.codeit.findex.dto.indexData.response.IndexDataDto;
import com.codeit.findex.dto.indexData.request.IndexDataDateRequest;
import com.codeit.findex.dto.indexData.request.IndexDataSaveRequest;
import com.codeit.findex.dto.indexData.request.IndexDataUpdateRequest;
import com.codeit.findex.service.IndexDataService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class IndexDataController {

    private final IndexDataService indexDataService;

    // 지수 데이터 등록
    @PostMapping("/index-data")
    public ResponseEntity<IndexDataDto> register(@RequestBody IndexDataSaveRequest request) {
        IndexDataDto indexDataDto = indexDataService.registerIndexData(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(indexDataDto);
        // return ResponseEntity.ok(indexDataDto);
    }

    // 지수 데이터 목록 조회
    @GetMapping("/index-data")
    public ResponseEntity<Page<IndexDataDto>> searchData(
            @PageableDefault(sort = "baseDate", direction = Sort.Direction.DESC) Pageable pageable,
            @ModelAttribute IndexDataDateRequest request) {

            Page<IndexDataDto> searchData = indexDataService.searchByIndexAndDate(request, pageable);
            return ResponseEntity.ok(searchData);
    }

    // 지수 데이터 수정
    @PatchMapping("/index-data/{id}")
    public ResponseEntity<IndexDataDto> updateData(
            @RequestBody IndexDataUpdateRequest request) {
        IndexDataDto updateData = indexDataService.updateIndexData(request);
        return ResponseEntity.ok(updateData);
    }

    // 지수 데이터 삭제
    @DeleteMapping("index-data/{id}")
    public ResponseEntity<IndexDataDto> delete(@PathVariable Long id) {
        indexDataService.deleteIndexData(id);
        return ResponseEntity.noContent().build();
    }

    // 지수 데이터 Export
//    @GetMapping("/index-data/export/csv")

}
