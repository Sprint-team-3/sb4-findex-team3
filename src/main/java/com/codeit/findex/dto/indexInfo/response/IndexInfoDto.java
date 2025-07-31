package com.codeit.findex.dto.indexInfo.response;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IndexInfoDto {
  private long id;
  private String indexClassification;
  private String indexName;
  private int employedItemsCount;
  private Date basePointInTime;
  private Double baseIndex;
  private String SourceType;
  private boolean favorite;
}
