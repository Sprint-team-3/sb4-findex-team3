package com.codeit.findex.dto.indexData.request;

import java.time.LocalDate;

public record IndexDataDownloadRequest(
    Long id,
    LocalDate baseDate, // 날짜
    double marketPrice, // 시가
    double closingPrice, // 종가
    double highPrice, // 고가
    double lowPrice, // 저가
    double versus, // 전일 대비 변동값
    double fluctuationRate, // 등락률
    double tradingQuantity, // 거래량
    Long tradingPrice, // 거래대금
    Long marketTotalAmount // 시가총액
    ) {}
