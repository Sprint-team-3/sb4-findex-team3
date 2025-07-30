package com.codeit.findex.dto.dashboard;

import java.util.Date;
import java.util.List;

// 지수 차트 데이터 DTO
public record IndexChartDto(
    long indexInfoId,
    String indexClassification,
    String indexName,
    String periodType,
    List<ChartDataPoint> dataPoints,
    List<ChartDataPoint> ma5DataPoints,
    List<ChartDataPoint> ma20DataPoints) {}

record ChartDataPoint(Date date, double number) {}
