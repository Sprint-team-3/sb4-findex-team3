package com.codeit.findex.dto.indexData.response;

import java.util.List;

public record CursorPageResponseIndexDataDto(
        List<IndexDataDto> data,
        String nextCursor,
        Long nextIdAfter,
        int size,
        Long totalElements,
        boolean hasNext
) {
}
