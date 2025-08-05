package com.codeit.findex.dto.indexData.request;

import java.time.LocalDate;
import java.util.UUID;

public record IndexDataUpdateRequest(
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
