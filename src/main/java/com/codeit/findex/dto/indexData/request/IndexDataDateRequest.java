package com.codeit.findex.dto.indexData.request;

import java.time.LocalDate;

public record IndexDataDateRequest(
    Long indexInfoId,
    LocalDate startDate,
    LocalDate endDate,
    Long idAfter,
    String cursor,
    String sortField,
    String sortDirection,
    Integer size

    //        Long indexInfo,
    ////        Date : 쿼리 파라미터로 날짜를 받을 때 포맷을 지정해준다고 함
    ////        @DateTimeFormat(pattern = "yyyy-MM-dd")
    //        LocalDate startDate,
    //        LocalDate endDate

    ) {}
