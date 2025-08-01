package com.codeit.findex.dto.indexData.response;

import com.codeit.findex.entityEnum.SourceType;
import java.time.LocalDate;

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
