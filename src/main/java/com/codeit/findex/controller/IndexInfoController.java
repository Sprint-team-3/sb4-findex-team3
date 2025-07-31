package com.codeit.findex.controller;

import com.codeit.findex.dto.indexInfo.request.IndexInfoCreateRequest;
import com.codeit.findex.dto.indexInfo.request.IndexInfoSearchCond;
import com.codeit.findex.dto.indexInfo.request.IndexInfoUpdateRequest;
import com.codeit.findex.dto.indexInfo.response.CursorPageResponseIndexInfoDto;
import com.codeit.findex.dto.indexInfo.response.ErrorResponse;
import com.codeit.findex.dto.indexInfo.response.IndexInfoDto;
import com.codeit.findex.dto.indexInfo.response.IndexInfoSummaryDto;
import com.codeit.findex.service.basic.BasicIndexInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/index-infos")
public class IndexInfoController {

    private final BasicIndexInfoService basicIndexInfoService;

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "지수 정보 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CursorPageResponseIndexInfoDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (유효하지 않은 필터 값 등)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                {
                  "timestamp": "2025-03-06T05:39:06.152068Z",
                  "status": 400,
                  "message": "잘못된 요청입니다.",
                  "details": "정렬 필드 값이 잘못되었습니다."
                }
            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                {
                  "timestamp": "2025-03-06T05:39:06.152068Z",
                  "status": 500,
                  "message": "서버 오류가 발생했습니다.",
                  "details": "예기치 못한 오류입니다."
                }
            """)
                    )
            )
    })
    @GetMapping
    public ResponseEntity<CursorPageResponseIndexInfoDto> getIndexInfoList(@ModelAttribute IndexInfoSearchCond searchCond) {
        return ResponseEntity.ok(basicIndexInfoService.findIndexInfoByCondition(searchCond));
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "지수 정보 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = IndexInfoDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "조회할 지수 정보를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                {
                  "timestamp": "2025-03-06T05:39:06.152068Z",
                  "status": 404,
                  "message": "지수 정보를 찾을 수 없습니다.",
                  "details": "해당 ID의 지수 정보가 존재하지 않습니다."
                }
            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<IndexInfoDto> getIndexInfo(@PathVariable long id) {
        return ResponseEntity.ok(basicIndexInfoService.findIndexInfoById(id));
    }

    @Operation(summary = "지수 정보 요약 목록 조회", description = "지수 ID, 분류, 이름만 포함한 전체 지수 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "지수 정보 요약 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = IndexInfoSummaryDto.class)),
                            examples = @ExampleObject(value = """
            [
              {
                "id": 1,
                "indexClassification": "KOSPI시리즈",
                "indexName": "IT 서비스"
              },
              {
                "id": 2,
                "indexClassification": "KOSDAQ시리즈",
                "indexName": "IT 하드웨어"
              }
            ]
            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
            {
              "timestamp": "2025-03-06T05:39:06.152068Z",
              "status": 500,
              "message": "서버 오류가 발생했습니다.",
              "details": "예기치 못한 오류입니다."
            }
            """)
                    )
            )
    })
    @GetMapping("/summaries")
    public ResponseEntity<List<IndexInfoSummaryDto>> getIndexInfoSummaries() {
        return ResponseEntity.ok(basicIndexInfoService.findIndexInfoSummaries());
    }

    @Operation(summary = "지수 정보 등록", description = "새로운 지수 정보를 등록합니다")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "지수 정보 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = IndexInfoDto.class),
                            examples = @ExampleObject(value = """
                {
                  "id": 1,
                  "indexClassification": "KOSPI시리즈",
                  "indexName": "IT 서비스",
                  "employedItemsCount": 200,
                  "basePointInTime": "2000-01-01",
                  "baseIndex": 1000.0,
                  "sourceType": "OPEN_API",
                  "favorite": true
                }
            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (필수 필드 누락 등)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                {
                  "timestamp": "2025-03-06T05:39:06.152068Z",
                  "status": 400,
                  "message": "잘못된 요청입니다.",
                  "details": "필수 값이 누락되었습니다."
                }
            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                {
                  "timestamp": "2025-03-06T05:39:06.152068Z",
                  "status": 500,
                  "message": "서버 오류가 발생했습니다.",
                  "details": "예기치 못한 오류입니다."
                }
            """)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<IndexInfoDto> registerIndexInfo(@RequestBody IndexInfoCreateRequest request) {
        IndexInfoDto response = basicIndexInfoService.registerIndexInfo(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "지수 정보 수정", description = "기존 지수 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "지수 정보 수정 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = IndexInfoDto.class),
                            examples = @ExampleObject(value = """
            {
              "id": 1,
              "indexClassification": "KOSPI시리즈",
              "indexName": "IT 서비스",
              "employedItemsCount": 200,
              "basePointInTime": "2000-01-01",
              "baseIndex": 1000.0,
              "sourceType": "OPEN_API",
              "favorite": true
            }
            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (유효하지 않은 필드 값 등)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
            {
              "timestamp": "2025-03-06T05:39:06.152068Z",
              "status": 400,
              "message": "잘못된 요청입니다.",
              "details": "필드 값이 유효하지 않습니다."
            }
            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "수정할 지수 정보를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
            {
              "timestamp": "2025-03-06T05:39:06.152068Z",
              "status": 404,
              "message": "지수 정보를 찾을 수 없습니다.",
              "details": "해당 ID의 지수 정보가 존재하지 않습니다."
            }
            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PatchMapping("/{id}")
    public ResponseEntity<IndexInfoDto> updateIndexInfo(@PathVariable long id,
                                                        @RequestBody IndexInfoUpdateRequest request) {
        IndexInfoDto response = basicIndexInfoService.updateIndexInfo(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "지수 정보 삭제", description = "지수 정보를 삭제합니다. 관련된 지수 데이터도 함께 삭제됩니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "지수 정보 삭제 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "삭제할 지수 정보를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
            {
              "timestamp": "2025-03-06T05:39:06.152068Z",
              "status": 404,
              "message": "지수 정보를 찾을 수 없습니다.",
              "details": "해당 ID의 지수 정보가 존재하지 않습니다."
            }
            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity deleteIndexInfo(@PathVariable long id) {
        basicIndexInfoService.deleteIndexInfo(id);
        return ResponseEntity.ok().build();
    }
}
