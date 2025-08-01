package com.codeit.findex.dto.indexData.request;

import com.codeit.findex.entity.IndexInfo;
import com.codeit.findex.entityEnum.SourceType;

import java.time.LocalDate;

public record IndexDataSaveRequest(
        // 지수, 날짜부터 상장 시가 총액까지 모든 속성을 입력해 지수 데이터를 등록하기
        Long indexInfoId, // 지수 정보
        LocalDate baseDate, // 기준 날짜
        SourceType sourceType, // 소스 타입
        double openPrice, // 시가
        double closingPrice, // 종가
        double highPrice, // 고가
        double lowPrice, // 저가
        double changeValue, // 전일대비 변동가
        double fluctuationRate, // 등락률
        int tradingVolume, // 거래량
        long tradingValue, // 거래대금
        long marketTotalAmount // 상장 시가총액
) {
}
