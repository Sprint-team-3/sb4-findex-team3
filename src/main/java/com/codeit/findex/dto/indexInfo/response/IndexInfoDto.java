package com.codeit.findex.dto.indexInfo.response;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IndexInfoDto {
  private long id;
  private String indexClassification;
  private String indexName;
  private int employedItemsCount;
  private LocalDate basepointInTime;
  private double baseIndex;
  private String sourceType;
  private Boolean favorite;
}
