package com.codeit.findex.domain.autosync.dto.response;

import java.util.List;

public record CursorPageResponseAutoSyncConfigDto(
    List<AutoSyncConfigDto> content,
    String nextCursor,
    long nextIdAfter,
    int size,
    long totalElements,
    boolean hasNext) {}
