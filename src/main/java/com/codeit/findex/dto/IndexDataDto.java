package com.codeit.findex.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record IndexDataDto(
    UUID id,
    UUID indexInfoId,
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