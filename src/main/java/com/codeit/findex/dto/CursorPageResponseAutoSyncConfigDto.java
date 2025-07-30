package com.codeit.findex.dto;

import java.util.List;

public record CursorPageResponseAutoSyncConfigDto(
        List<AutoSyncConfigDto> content,
        String nextCursor,
        Long nextIdAfter,
        int size,
        Long totalElements,
        boolean hasNext
) {
}
