package com.codeit.findex.dto.indexInfo.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CursorPageResponseIndexInfoDto {
  private List<IndexInfoDto> content;

  private String nextCursor;
  private Long nextIdAfter;
  private int size;
  private long totalElements;
  private boolean hasNext;
}
