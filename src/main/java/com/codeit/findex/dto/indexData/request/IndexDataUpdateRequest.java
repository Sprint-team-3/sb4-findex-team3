package com.codeit.findex.dto.indexData.request;

import java.time.LocalDate;
import java.util.UUID;

public record IndexDataUpdateRequest(
        // 지수, 날짜를 제외한 모든 속성을 수정할 수 있다
        // double을 Double로 바꿨음
        Double marketPrice, // 시가
        Double closingPrice, // 종가 *
        Double highPrice, // 고가 *
        Double lowPrice, // 저가 *
        Double versus,
        Double fluctuationRate, // 등락률 *
        Integer tradingQuantity,
        Long tradingPrice,
        Long marketTotalAmount
) {
}
