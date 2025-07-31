package com.codeit.findex.dto;

import com.codeit.findex.entityEnum.SourceType;
import java.time.LocalDate;
import java.util.UUID;

public record IndexDataDto(
    Long id,
    Long indexInfoId,
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
