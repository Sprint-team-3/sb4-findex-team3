package com.codeit.findex.dto.indexData.response;

import com.codeit.findex.entityEnum.SourceType;
import java.time.LocalDate;
import lombok.Setter;

public record IndexDataDto(
    Long id, // 지수 데이터 ID
    Long indexInfoId,
    LocalDate baseDate,
    SourceType sourceType,
    double marketPrice,
    double closingPrice,
    double highPrice,
    double lowPrice,
    double versus,
    double fluctuationRate,
    Long tradingQuantity,
    Long tradingPrice,
    Long marketTotalAmount) {}
