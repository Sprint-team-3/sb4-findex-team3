package com.codeit.findex.dto.indexInfo.response;

import com.codeit.findex.entityEnum.SourceType;
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
  private Long id;
  private String indexClassification;
  private String indexName;
  private int employedItemsCount;
  private LocalDate basePointInTime;
  private double baseIndex;
  private SourceType sourceType;
  private Boolean favorite;
}
