package com.codeit.findex.dto;

import com.codeit.findex.entity.IndexInfo;
import com.codeit.findex.entity.base.SourceType;

import java.time.LocalDate;

public record IndexDataDto(

        IndexInfo indexInfo,
        LocalDate baseDate,
        SourceType type,
        double openPrice,
        double closingPrice,
        double highPrice,
        double lowPrice,
        double changeValue,
        double fluctuationRate,
        int tradingVolume,
        long tradingValue,
        long marketToTalAmount
) {
}
