package com.codeit.findex.domain.autosync.dto.response;

public record AutoSyncConfigDto(
    long id, long indexInfoId, String indexClassification, String indexName, boolean enabled) {}
