package com.codeit.findex.dto.integration;

import java.util.List;

public record CursorPageResponseSyncJobDto(
    List<SyncJobDto> content,
    String nextCursor,
    Long nextIdAfter,
    int size,
    long totalElements,
    boolean hasNext) {}
