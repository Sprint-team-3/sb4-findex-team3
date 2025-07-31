package com.codeit.findex.dto.indexInfo.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class IndexInfoSearchCond {

    private String indexClassification;
    private String indexName;
    private Boolean favorite;

    private long idAfter;
    private String cursor;

    private String sortField = "indexClassification";  // 기본값
    private String sortDirection = "asc";
    private int size = 10;
}
