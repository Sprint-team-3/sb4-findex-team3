package com.codeit.findex.dto.dashboard;

import java.math.BigDecimal;
import java.util.UUID;

public record PerformanceDto(
    UUID indexInfoId,
    String indexClassification,
    String indexName,
    double versus,
    double fluctuationRate,
    double currentPrice,
    double beforePrice) {}
