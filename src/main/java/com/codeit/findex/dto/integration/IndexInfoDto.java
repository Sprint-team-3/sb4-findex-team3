package com.codeit.findex.dto.integration;

import com.codeit.findex.entityEnum.SourceType;
import java.time.LocalDate;
import java.util.UUID;

public record IndexInfoDto(
    UUID id,
    String indexClassification,
    String indexName,
    int employedItemsCount,
    LocalDate basepointInTime,
    double baseIndex,
    SourceType sourceType,
    boolean favorite) {}
