package com.codeit.findex.dto;

public record AutoSyncConfigDto(
    Long id, Long indexInfoId, String indexClassification, String indexName, boolean enabled) {}
