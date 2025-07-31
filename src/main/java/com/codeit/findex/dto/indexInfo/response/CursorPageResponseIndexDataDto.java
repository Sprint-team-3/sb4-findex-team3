package com.codeit.findex.dto.indexInfo.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CursorPageResponseIndexDataDto {
    private List<IndexInfoDto> content;

    private String nextCursor;
    private long nextIdAfter;
    private int size;
    private long totalElements;
    private boolean hasNext;
}
