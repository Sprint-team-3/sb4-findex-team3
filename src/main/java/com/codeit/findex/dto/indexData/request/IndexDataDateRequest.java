package com.codeit.findex.dto.indexData.request;

import java.time.LocalDate;
import java.util.UUID;

public record IndexDataDateRequest(
        long indexInfo,
//        Date : 쿼리 파라미터로 날짜를 받을 때 포맷을 지정해준다고 함
//        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate startDate,
        LocalDate endDate
) {
}