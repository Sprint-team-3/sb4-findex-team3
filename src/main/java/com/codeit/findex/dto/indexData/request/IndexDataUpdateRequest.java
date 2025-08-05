package com.codeit.findex.dto.indexData.request;

import java.time.LocalDate;
import java.util.UUID;

public record IndexDataUpdateRequest(
//        // 지수, 날짜를 제외한 모든 속성을 수정할 수 있다
//        long id,
//        LocalDate baseDate,
////        SourceType type, // 소스 타입, 수정할 수 없음
//        double openPrice, // 시가 *
//        double closingPrice, // 종가 *
//        double highPrice, // 고가 *
//        double lowPrice, // 저가 *
//        int tradingVolume, // 거래량 *
//        double changeValue, // 전일대비 변동값 *
//        double fluctuationRate // 등락률 *
////        long tradingValue, // 거래대금, 프로토 타입에서는 수정할 수 없음
////        long marketTotalAmount // 시가총액, 수정할 수 없음


        // 지수, 날짜를 제외한 모든 속성을 수정할 수 있다
        double marketPrice, // 시가
        double closingPrice, // 종가 *
        double highPrice, // 고가 *
        double lowPrice, // 저가 *
        double versus,
        double fluctuationRate, // 등락률 *
        Integer tradingQuantity,
        Long tradingPrice,
        Long marketTotalAmount
) {
}
