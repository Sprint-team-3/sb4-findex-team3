package com.codeit.findex.mapper;

import com.codeit.findex.dto.indexData.request.IndexDataDownloadRequest;
import com.codeit.findex.entity.IndexData;

public class CSVStringMapper {
  public static String[] mapper(IndexData indexData) { // 메모리 낭빈데?
    return new String[] {
      String.valueOf(indexData.getBaseDate()),
      String.valueOf(indexData.getMarketPrice()),
      String.valueOf(indexData.getClosingPrice()),
      String.valueOf(indexData.getHighPrice()),
      String.valueOf(indexData.getLowPrice()),
      String.valueOf(indexData.getVersus()),
      String.valueOf(indexData.getFluctuationRate()),
      String.valueOf(indexData.getTradingQuantity()),
      String.valueOf(indexData.getTradingPrice()),
      String.valueOf(indexData.getMarketTotalAmount())
    };
  }
}
