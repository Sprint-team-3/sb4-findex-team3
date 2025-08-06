package com.codeit.findex.dto.indexData.request;

import java.time.LocalDate;

public record IndexDataDownloadRequest(
    Long id,
    LocalDate baseDate, // 날짜
    double openPrice, // 시가
    double closingPrice, // 종가
    double highPrice, // 고가
    double lowPrice, // 저가
    double changeValue, // 전일 대비 변동값
    double fluctuationRate, // 등락률
    double tradingVolume, // 거래량
    Long tradingValue, // 거래대금
    Long marketTotalAmount // 시가총액
    ) {}
