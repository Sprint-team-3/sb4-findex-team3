package com.codeit.findex.dto.indexInfo.response;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IndexInfoDto {
    private long id;
    private String indexClassification;
    private String indexName;
    private LocalDate basePointInTime;
    private int employedItemsCount;
    private Double baseIndex;
    private String SourceType;
    private Boolean favorite;
}
