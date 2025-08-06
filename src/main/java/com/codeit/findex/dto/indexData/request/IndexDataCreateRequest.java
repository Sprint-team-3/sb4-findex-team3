package com.codeit.findex.dto.indexData.request;

import com.codeit.findex.entityEnum.SourceType;

import java.time.LocalDate;

public record IndexDataCreateRequest(
    Long indexInfoId, // 지수 정보
    LocalDate baseDate, // 기준 날짜
    double marketPrice, // 시가
    double closingPrice, // 종가
    double highPrice, // 고가
    double lowPrice, // 저가
    double versus,
    double fluctuationRate, // 등락률
    Integer tradingQuantity,
    Long tradingPrice,
    Long marketTotalAmount // 상장 시가총액
) {
}
