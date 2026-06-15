package com.codeit.findex.domain.dashboard.dto.response;

public record PerformanceDto(
    Long indexInfoId,
    String indexClassification,
    String indexName,
    double versus,
    double fluctuationRate,
    double currentPrice,
    double beforePrice) {}
