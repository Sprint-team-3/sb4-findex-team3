package com.codeit.findex.dto.indexInfo.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IndexInfoSummaryDto {
    private Long id;
    private String indexClassification;
    private String indexName;
}
