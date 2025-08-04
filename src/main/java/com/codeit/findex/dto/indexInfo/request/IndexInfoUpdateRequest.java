package com.codeit.findex.dto.indexInfo.request;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class IndexInfoUpdateRequest {
  private Integer employedItemsCount;
  private LocalDate basePointInTime;
  private Double baseIndex;
  private Boolean favorite;
}
