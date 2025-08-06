package com.codeit.findex.dto.indexInfo.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IndexInfoSummaryDto {
  private long id;
  private String indexClassification;
  private String indexName;
}
