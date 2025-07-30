package com.codeit.findex.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record IndexInfoDto(
    UUID infoId,
    String indexClassification,
    String indexName,
    int employedItemsCount,
    LocalDateTime basepointInTime,
    double baseIndex,
    String sourceType,
    boolean favorite) {}