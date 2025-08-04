package com.codeit.findex.dto.autosync.response;

public record AutoSyncConfigDto(
    long id, long indexInfoId, String indexClassification, String indexName, boolean enabled) {}
