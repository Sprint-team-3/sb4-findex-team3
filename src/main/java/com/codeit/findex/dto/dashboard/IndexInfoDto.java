package com.codeit.findex.dto.dashboard;

import java.time.LocalDateTime;

public record IndexInfoDto(
    long infoId,
    String indexClassification,
    String indexName,
    int employedItemsCount,
    LocalDateTime basepointInTime,
    double baseIndex,
    String sourceType,
    boolean favorite) {}
