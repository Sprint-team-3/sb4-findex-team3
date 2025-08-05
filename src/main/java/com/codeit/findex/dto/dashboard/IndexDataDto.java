package com.codeit.findex.dto.dashboard;

import java.time.LocalDateTime;

public record IndexDataDto(
    Long id,
    Long indexInfoId,
    LocalDateTime baseDate, // date
    String sourceType,
    double closingPrice // current price
    //      double marketPrice,
    //      double highPrice,
    //      double lowPrice,
    //      double versus,
    //      double fluctuationRate
    //      ,
    //      long tradingQuantity,
    //      long tradingPrice,
    //      long marketTotalAmount
    ) {}
