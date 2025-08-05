package com.codeit.findex.dto.indexData.response;

import java.util.List;

public record CursorPageResponseIndexDataDto(
        List<IndexDataDto> data,
        String nextCursor,
        Integer nextIdAfter,
        Integer size,
        Integer totalElements,
        boolean hasNext
) {
}
