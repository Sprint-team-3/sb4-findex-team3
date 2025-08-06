package com.codeit.findex.mapper;

import com.codeit.findex.entity.IndexData;

public class CSVStringMapper {
  public static String[] mapper(IndexData indexData) { // 메모리 낭빈데?
    return new String[] {
      String.valueOf(indexData.getBaseDate()),
      String.valueOf(indexData.getOpenPrice()),
      String.valueOf(indexData.getClosingPrice()),
      String.valueOf(indexData.getHighPrice()),
      String.valueOf(indexData.getLowPrice()),
      String.valueOf(indexData.getChangeValue()),
      String.valueOf(indexData.getFluctuationRate()),
      String.valueOf(indexData.getTradingVolume()),
      String.valueOf(indexData.getTradingValue()),
      String.valueOf(indexData.getMarketTotalAmount())
    };
  }
}
