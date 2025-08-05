package com.codeit.findex.dto.indexData.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class IndexDataSortPageRequest {
    private Long indexInfoId; //지수 정보 id
    private LocalDate startDate; // 시작일자
    private LocalDate endDate; // 종료일자
    private Long idAfter; // 이전 페이지 마지막 요소의 id
    private String cursor; // 커서(다음 페이지 시작점)

    // 정렬 필드, Default value = baseDate
    // (baseDate, marketPrice, closingPrice, highPrice, lowPrice, versus,
    // fluctuationRate, tradingQuantity, tradingPrice, marketTotalAmount)
    private String sortField;
    private String sortDirection; // 정렬 방향, Default = desc
    private Integer size; // 페이지 크기, default = 10
}
