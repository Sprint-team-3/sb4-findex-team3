package com.codeit.findex.dto;

import com.codeit.findex.entityEnum.SourceType;
import java.time.LocalDate;

public record IndexInfoDto(
    long id,
    String indexClassification,
    String indexName,
    int employedItemsCount,
    LocalDate basepointInTime,
    double baseIndex,
    SourceType sourceType,
    boolean favorite) {}
