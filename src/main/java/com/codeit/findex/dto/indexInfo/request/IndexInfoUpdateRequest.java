package com.codeit.findex.dto.indexInfo.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class IndexInfoUpdateRequest {
    private Integer employedItemsCount;
    private LocalDate basePointInTime;
    private Double baseIndex;
    private Boolean favorite;
}
