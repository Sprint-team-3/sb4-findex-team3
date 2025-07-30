package com.codeit.findex.dto.indexInfo.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class IndexInfoUpdateRequest {
    private Integer employedItemsCount;
    private Date basePointInTime;
    private Double baseIndex;
    private Boolean favorite;
}
