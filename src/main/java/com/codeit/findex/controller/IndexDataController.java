package com.codeit.findex.controller;

import com.codeit.findex.dto.indexData.request.IndexDataSortPageRequest;
import com.codeit.findex.dto.indexData.response.CursorPageResponseIndexDataDto;
import com.codeit.findex.dto.indexData.response.IndexDataDto;
import com.codeit.findex.dto.indexData.request.IndexDataDateRequest;
import com.codeit.findex.dto.indexData.request.IndexDataCreateRequest;
import com.codeit.findex.dto.indexData.request.IndexDataUpdateRequest;
import com.codeit.findex.service.IndexDataService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class IndexDataController {

    private final IndexDataService indexDataService;

    // 지수 데이터 등록
    // =============================== 완료 ==================================
    @PostMapping("/index-data")
    public ResponseEntity<IndexDataDto> register(@RequestBody IndexDataCreateRequest request) {
        IndexDataDto indexDataDto = indexDataService.registerIndexData(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(indexDataDto);
        // return ResponseEntity.ok(indexDataDto);
    }

    // 지수 데이터 목록 조회
    // 여기 안에서 커서 해야함 <<<<<<<<<<<<<<<<<<
    @GetMapping("/index-data")
    public ResponseEntity<CursorPageResponseIndexDataDto> searchData(
            @PageableDefault(sort = "baseDate", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) Long indexInfoId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Integer idAfter,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "baseDate") String sortField,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(defaultValue = "10") Integer size
    )
    {
        CursorPageResponseIndexDataDto cursorPageResponseIndexDataDto =
            indexDataService.searchByIndexAndDate(
                    indexInfoId, startDate, endDate, idAfter, cursor,
                    sortField, sortDirection, size);

//            Page<IndexDataDto> searchData = indexDataService.searchByIndexAndDate(request, pageable);
            return ResponseEntity.ok(cursorPageResponseIndexDataDto);
    }

    // 지수 데이터 수정
    // =============================== 완료 ==================================
    @PatchMapping("/index-data/{id}")
    public ResponseEntity<IndexDataDto> updateData(
            @RequestBody IndexDataUpdateRequest request,
        @PathVariable Long id) {
        IndexDataDto updateData = indexDataService.updateIndexData(request, id);
        return ResponseEntity.ok(updateData);
    }

    // 지수 데이터 삭제
    // =============================== 완료 ==================================
    @DeleteMapping("index-data/{id}")
    public ResponseEntity<IndexDataDto> delete(@PathVariable Long id) { // @RequestParam은 쓰면 안되는건가?
        indexDataService.deleteIndexData(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 지수 데이터 Export
     * 지수 데이터를 CSV파일로 Export 할 수 있음
     * Export할 지수 데이터를 지수 데이터 목록 조회와 같은 규칙으로 필터링 및 정렬할 수 있음
     * 페이지네이션은 고려하지 않음
     *
     * 지수정보 ID : indexInfoId
     * 시작일자 : startDate
     * 종료일자 : endDate
     * 정렬필드 : baseDate, marketPrice, closingPrice, highPrice, lowPrice, versus, fluctuationArte, tradingQuantity, tradingPrice, marketTotalAmount
     * 정렬방향 : Default(desc)
     */
    @GetMapping("index-data/export/csv")
    public ResponseEntity<byte[]> exportCsv(
        @RequestParam("indexInfoId") Long indexInfoId,
        @RequestParam("startDate") String startDate,
        @RequestParam("endDate") String endDate,
        @RequestParam("sortField") String sortField,
        @RequestParam("sortDirection") String sortDirection
    ) {
        byte[] response = indexDataService.downloadIndexData();

        if(response == null) {
            System.out.println("꺄아악 IndexData가 아무것도 없어요!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        HttpHeaders headers = new HttpHeaders(); // HttpHeaders 객체를 만듬
        // Headername, HeaderValue를 적는데, 말 그대로 header 이름이랑 다운로드되는 file의 이름을 입력한다.
        headers.add("Content-Disposition", "attachment; filename=IndexData.csv");
        // public ResponseEntity(@Nullable T body, @Nullable MultiValueMap<String, String> headers, HttpStatusCode statusCode)
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }
}
