package com.codeit.findex.dto.dashboard;

// 순위가 포함된 지수 성과 정보 DTO
public record RankedIndexPerformanceDto(
    PerformanceDto performance,
    Integer rank
) {

}

