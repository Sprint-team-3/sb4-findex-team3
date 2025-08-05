package com.codeit.findex.dto.integration;

import java.util.List;

public record CursorPageResponseSyncJobDto(
    List<SyncJobDto> content,
    String nextCursor,
    Long nextIdAfter,
    int size,
    long totalElements,
    boolean hasNext) {
  public static CursorPageResponseSyncJobDto of(
    List<SyncJobDto> content,
    String nextCursor,
    Long nextIdAfter,
    int size,
    long totalElements,
    boolean hasNext
) {
  return new CursorPageResponseSyncJobDto(content, nextCursor, nextIdAfter, size, totalElements, hasNext);
  }
}
