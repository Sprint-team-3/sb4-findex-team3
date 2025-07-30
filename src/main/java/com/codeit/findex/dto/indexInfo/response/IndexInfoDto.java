package com.codeit.findex.dto.indexInfo.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IndexInfoDto {
    private UUID id;
    private String indexClassification;
    private String indexName;
    private Integer employedItemsCount;
    private LocalDate basePointInTime;
    private Double baseIndex;
    private String SourceType;
    private Boolean favorite;
}
