package com.codeit.findex.controller;

import com.codeit.findex.dto.IndexDataDto;
import com.codeit.findex.request.IndexDataDateRequest;
import com.codeit.findex.service.IndexDataService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/index-data")
public class IndexDataController {

    private final IndexDataService indexDataService;

    @GetMapping("/search")
    public ResponseEntity<Page<IndexDataDto>> search(
            @PageableDefault(sort = "baseDate", direction = Sort.Direction.DESC) Pageable pageable,
            @ModelAttribute IndexDataDateRequest request) {

            Page<IndexDataDto> searchData = indexDataService.searchByIndexAndDate(request, pageable);
            return ResponseEntity.ok(searchData);
    }
}
