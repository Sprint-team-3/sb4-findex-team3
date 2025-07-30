package com.codeit.findex.dto.autosync.response;


public record AutoSyncConfigDto(
        Long id,
        long indexInfoId,
        String indexClassification,
        String indexName,
        boolean enabled
) {
}
