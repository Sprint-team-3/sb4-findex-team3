package com.codeit.findex.dto;

import com.codeit.findex.entityEnum.SourceType;
import java.time.LocalDate;

public record IndexDataDto(
    long id,
    long indexInfoId,
    LocalDate baseDate,
    SourceType sourceType,
    double marketPrice,
    double closingPrice,
    double highPrice,
    double lowPrice,
    double versus,
    double fluctuationRate,
    long tradingQuantity,
    long tradingPrice,
    long marketTotalAmount) {}
