package com.codeit.findex.dto;

import java.util.UUID;

public record AutoSyncConfigDto(
        long id,
        long indexInfoId,
        String indexClassification,
        String indexName,
        boolean enabled
) {
}
