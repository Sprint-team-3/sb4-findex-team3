package com.codeit.findex.dto.dashboard.response;

public record PerformanceDto(
    Long indexInfoId,
    String indexClassification,
    String indexName,
    double versus,
    double fluctuationRate,
    double currentPrice,
    double beforePrice) {}
