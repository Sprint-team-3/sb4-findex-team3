package com.codeit.findex.dto;

import java.util.UUID;

public record AutoSyncConfigDto(
        Long id,
        Long indexInfoId,
        String indexClassification,
        String indexName,
        boolean enabled
) {
}
