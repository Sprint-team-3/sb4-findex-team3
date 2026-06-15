package com.codeit.findex.domain.indexData.dto.response;

import com.codeit.findex.domain.base.entityEnum.SourceType;
import java.time.LocalDate;

public record IndexDataDto(
    Long id, // 지수 데이터 ID
    Long indexInfoId,
    LocalDate baseDate,
    SourceType sourceType,
    double marketPrice, // 시가
    double closingPrice,
    double highPrice,
    double lowPrice,
    double versus,
    double fluctuationRate,
    Long tradingQuantity, // 거래량
    Long tradingPrice,
    Long marketTotalAmount) {}
