package com.codeit.findex.dto.indexInfo.request;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class IndexInfoCreateRequest {

  @NotBlank(message = "분류를 입력해주세요.")
  private String indexClassification;

  @NotBlank(message = "지수명을 입력해주세요.")
  private String indexName;

  @NotBlank(message = "채용 종목 수는 0보다 커야 합니다.")
  private int employedItemsCount;

  @NotNull(message = "기준 시점은 필수입니다.")
  private LocalDate basePointInTime;

  private double baseIndex;
  private Boolean favorite;
}
