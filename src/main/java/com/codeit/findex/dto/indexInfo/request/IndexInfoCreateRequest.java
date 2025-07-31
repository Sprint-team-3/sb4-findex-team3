package com.codeit.findex.dto.indexInfo.request;

import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class IndexInfoCreateRequest {
  private String indexClassification;
  private String indexName;
  private Integer employedItemsCount;
  private Date basePointInTime;
  private Double baseIndex;
  private Boolean favorite;
}
