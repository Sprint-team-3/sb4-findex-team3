package com.codeit.findex.dto.indexData.request;

import java.time.LocalDate;

public record IndexDataDateRequest(
    Long indexInfoId, //지수 정보 id
    LocalDate startDate, // 시작일자
    LocalDate endDate, // 종료일자
    Long idAfter, // 이전 페이지 마지막 요소의 id
    String cursor, // 커서(다음 페이지 시작점)

    String sortField, // 정렬 필드, Default value = baseDate
    String sortDirection, // 정렬 방향
    Integer size // 페이지 크기
    ) {}
